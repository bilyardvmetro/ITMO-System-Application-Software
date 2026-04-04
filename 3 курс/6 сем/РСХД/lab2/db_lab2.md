<div style="text-align: center; margin-top: 60px; line-height: 1.8;">
  <div>Федеральное государственное автономное образовательное</div>
  <div>учреждение высшего образования</div>
  <div>«Национальный исследовательский университет ИТМО»</div>
</div>

<div style="text-align: center; margin-top: 140px; line-height: 1.8;">
  <div><b>Отчёт</b></div>
  <div><b>По лабораторной работе №2</b></div>
  <div>по дисциплине Распределенные системы хранения данных»</div>
  <div><b>Вариант: 13202</b></div>
</div>

<div style="margin-top: 260px; width: 100%; display: flex; justify-content: flex-end;">
  <div style="text-align: right; line-height: 2; min-width: 420px;">
    <div>Работу выполнили:</div>
    <div>Поленов Кирилл Александрович</div>
    <div>Молчанов Федор Денисович</div>
    <div>Группа P3313</div>
    <div>Работу принял:</div>
    <div>Максимов Андрей Николаевич</div>
  </div>
</div>

<div style="text-align: center; margin-top: 260px;">
  г. Санкт-Петербург 2026
</div>

## Задание

<img src="рсхд лаб2.png">

## Этап 1. Резервное копирование

### Включить для СУБД режим архивирования WAL

**На основном узле `postgres1@pg113`**

- Подготавливаем каталог для копирования

```zsh
export PGDATA=$HOME/kqz19
export PGPORT=9041

mkdir -p "$HOME/wal_archive"
chmod 700 "$HOME/wal_archive"

ls -ld "$PGDATA" "$HOME/wal_archive"
```

- Генерируем ssh ключи и копируем их на `pg118` для перекидывания по scp без запроса пароля

> ed_22519 - алгоритм
> -N "" - создать ключ без пароля

```zsh
ssh-keygen -t ed25519 -f ~/.ssh/id_ed25519 -N ""
ssh-copy-id -i ~/.ssh/id_ed25519.pub postgres2@pg118
```

проверка

```zsh
[postgres1@pg113 ~]$ ssh postgres2@pg118 'hostname && whoami'

pg118.cs.ifmo.ru
postgres2
```

**На резервном узле `postgres2@pg118`**

```zsh
mkdir -p "$HOME/wal_archive"
mkdir -p "$HOME/basebackup"
mkdir -p "$HOME/kqz19"

chmod 700 "$HOME/wal_archive" "$HOME/basebackup" "$HOME/kqz19"

ls -ld "$HOME/wal_archive" "$HOME/basebackup" "$HOME/kqz19"
```

**На основном узле `postgres1@pg113`**

### Настроить копирование WAL (scp) на резервный узел

**`postgresql.conf`**

```zsh
cp "$PGDATA/postgresql.conf" "$PGDATA/postgresql.conf.lab2-stage1"

cat >> "$PGDATA/postgresql.conf" <<'EOF'

# === Lab 2 backup settings ===
wal_level = replica
archive_mode = on
archive_timeout = 60s

archive_command = 'scp %p postgres2@pg118:/var/db/postgres2/wal_archive/%f'
EOF
```

> %p — полный путь к текущему WAL-файлу на основном узле
> %f — имя файла

проверка

```zsh
tail -n 20 "$PGDATA/postgresql.conf"
```

**`pg_hba.conf`**

Разрешаем локальные TCP-подключения по паролю и подключение узла `pg118` (192.168.11.118/32 ) для пользователя `replicator` (`pg_basebackup` требует прав `REPLICATION`) для непрерывного WAL архивирования на `pg118`.

**Для будущей настройки `pg118`**

Наш юзер на `pg118` - `postgres2`.

- Скопированный нами кластер `pg113` не содержит внутри себя такого юзера.
- Мы не можем менять юзера на виртуалке с `postgres2` на `postgres1`
- Подключаться к базе через `ident` невозможно из-за того, что `ident` сервис не работает на гелиосе

В совокупнсоти этих фактов было принято решение ослабить `pg_hba.conf` и использовать `trust` в качестве метода аутентификации

```zsh
cp "$PGDATA/pg_hba.conf" "$PGDATA/pg_hba.conf.lab2-stage1"

cat > "$HOME/kqz19/pg_hba.conf" <<'EOF'
# TYPE  DATABASE        USER        ADDRESS         METHOD

# разрешаем role_lab подключаться по паролю через Unix socket
local   fakebrownwood   role_lab                    scram-sha-256

# 1) Unix-domain сокет: peer
local   all             all                         peer

# 2) TCP/IP к любому IP-адресу узла
host    all             all         0.0.0.0/0       trust
host    all             all         ::/0            trust

# для узла pg118
host    replication     replicator  192.168.11.118/32    scram-sha-256

# для первичной резервной копии
host    replication     replicator  127.0.0.1/32    scram-sha-256
host    replication     replicator  ::1/128         scram-sha-256
EOF
```

Перезагружаем сервер

```zsh
pg_ctl -D "$PGDATA" restart
```

проверка

```zsh
[postgres1@pg113 ~]$ psql -p "$PGPORT" -d postgres -c "show wal_level; show archive_mode; show archive_command;"
 wal_level 
-----------
 replica
(1 строка)

 archive_mode 
--------------
 on
(1 строка)

                     archive_command                     
---------------------------------------------------------
 scp %p postgres2@pg118:/var/db/postgres2/wal_archive/%f
(1 строка)

```

Создаем пользователя для репликации

```zsh
psql -p "$PGPORT" -d postgres -v ON_ERROR_STOP=1 <<'SQL'
CREATE ROLE replicator
  WITH REPLICATION
       LOGIN
       PASSWORD 'Replicator_123';
SQL
```

проверка

```zsh
[postgres1@pg113 ~]$ psql -p "$PGPORT" -d postgres -c "\du replicator"
      Список ролей
  Имя роли  |  Атрибуты  
------------+------------
 replicator | Репликация
```

Проверяем, что архивирование действительно работает

**На основном узле `postgres1@pg113`**

```zsh
psql -p "$PGPORT" -d postgres -c "SELECT pg_switch_wal();"

 pg_switch_wal 
---------------
 0/3000000
(1 строка)

```

**На резервном узле `postgres2@pg118`**

```zsh
ls -lh /var/db/postgres2/wal_archive | tail

total 4090
-rw-------  1 postgres2 postgres   16M  7 марта 16:57 000000010000000000000001
-rw-------  1 postgres2 postgres   16M  8 марта 01:09 000000010000000000000002
-rw-------  1 postgres2 postgres   16M  8 марта 01:12 000000010000000000000003
```

### Создать первоначальную резервную копию (pg_basebackup)

**На основном узле `postgres1@pg113`**

Создаем первичную копию через `pg_basebackup`

```zsh
rm -rf "$HOME/basebackup_primary"
mkdir -p "$HOME/basebackup_primary"
chmod 700 "$HOME/basebackup_primary"

pg_basebackup \
  -h localhost \
  -p "$PGPORT" \
  -U replicator \
  -D "$HOME/basebackup_primary" \
  -Ft \
  -P
```

> -Ft — tar format
> -P — прогресс

проверка

```zsh
ls -lh "$HOME/basebackup_primary"

total 6835
-rw-------  1 postgres1 postgres  7,6M 26 марта 17:48 16386.tar
-rw-------  1 postgres1 postgres   19K 26 марта 17:48 16387.tar
-rw-------  1 postgres1 postgres   59K 26 марта 17:48 16388.tar
-rw-------  1 postgres1 postgres  187K 26 марта 17:48 backup_manifest
-rw-------  1 postgres1 postgres   23M 26 марта 17:48 base.tar
-rw-------  1 postgres1 postgres   16M 26 марта 17:48 pg_wal.tar
```

### Скопировать на резервный узел (rsync)

Передаем первоначальную резервную копию на резервный узел через rsync

```zsh
rsync -av --delete "$HOME/basebackup_primary"/ postgres2@pg118:/var/db/postgres2/basebackup/

sending incremental file list
./
16386.tar
16387.tar
16388.tar
backup_manifest
base.tar
pg_wal.tar

sent 49.130.600 bytes  received 133 bytes  32.753.822,00 bytes/sec
total size is 49.118.175  speedup is 1,00
```

**Проверка на резервном узле `postgres2@pg118`**

```zsh
ssh postgres2@pg118 'ls -lh /var/db/postgres2/basebackup'

total 6835
-rw-------  1 postgres2 postgres  7,6M 26 марта 17:48 16386.tar
-rw-------  1 postgres2 postgres   19K 26 марта 17:48 16387.tar
-rw-------  1 postgres2 postgres   59K 26 марта 17:48 16388.tar
-rw-------  1 postgres2 postgres  187K 26 марта 17:48 backup_manifest
-rw-------  1 postgres2 postgres   23M 26 марта 17:48 base.tar
-rw-------  1 postgres2 postgres   16M 26 марта 17:48 pg_wal.tar
```

### Подсчет объема резервных копий

> Подсчитать, каков будет объем резервных копий спустя месяц работы системы, исходя из следующих условий:
> • Средний объем новых данных в БД за сутки: 800МБ.
> • Средний объем измененных данных за сутки: 850МБ.

Объём новых данных за месяц:
800 МБ × 30 = 24000 МБ

Объём WAL, обусловленный изменениями и добавлением данных:
(800 МБ + 850 МБ) × 30 = 49500 МБ

Тогда суммарный объём хранилища под схему
«полная резервная копия + непрерывное архивирование WAL»
составит примерно:

24000 + 49500 = 73500 МБ ≈ 71.8 ГиБ

## Этап 2. Потеря основного узла

> Этот сценарий подразумевает полную недоступность основного узла. Необходимо восстановить работу СУБД на РЕЗЕРВНОМ узле, продемонстрировать успешный запуск СУБД и доступность данных.

### Имитируем сбой primary узла `pg113`

```zsh
pg_ctl -D "$PGDATA" stop

ожидание завершения работы сервера.... готово
сервер остановлен
```

Проверка

```zsh
pg_ctl -D "$PGDATA" status

pg_ctl: сервер не работает
```

### Подготавливаем каталог данных на резервном узле `pg118`

```zsh
rm -rf "$HOME/kqz19"
mkdir -p "$HOME/kqz19"
chmod 700 "$HOME/kqz19"
```

### Распаковываем base backup

```zsh
cd "$HOME/kqz19"
tar -xf "$HOME/basebackup/base.tar"
tar -xf "$HOME/basebackup/pg_wal.tar"
```

Исправление ссылок табличных пространств

```zsh
# удаляем старые ссылки (они указывают на pg113)
rm -f "$HOME/kqz19/pg_tblspc/16386"
rm -f "$HOME/kqz19/pg_tblspc/16387"
rm -f "$HOME/kqz19/pg_tblspc/16388"

# создаём каталоги для tablespace на pg118
rm -rf $HOME/ndx81 $HOME/beb52 $HOME/smq7
mkdir -p $HOME/ndx81 $HOME/beb52 $HOME/smq7
chmod 700 $HOME/ndx81 $HOME/beb52 $HOME/smq7

# создаём новые ссылки
ln -s $HOME/ndx81 "$HOME/kqz19/pg_tblspc/16386"
ln -s $HOME/beb52 "$HOME/kqz19/pg_tblspc/16387"
ln -s $HOME/smq7  "$HOME/kqz19/pg_tblspc/16388"
```

Распаковка tablespace

```zsh
tar -xf $HOME/basebackup/16386.tar -C $HOME/ndx81
tar -xf $HOME/basebackup/16387.tar -C $HOME/beb52
tar -xf $HOME/basebackup/16388.tar -C $HOME/smq7
```

### Настраиваем recovery

```zsh
cat >> "$HOME/kqz19/postgresql.auto.conf" <<'EOF'
restore_command = 'cp /var/db/postgres2/wal_archive/%f %p'
port = 9042
EOF
```

Создаём `recovery.signal`

> чтобы при старте postgres вошел в режим recovery и применил WAL из архива.

```zsh
touch "$HOME/kqz19/recovery.signal"
```

### Запускаем PostgreSQL на резервном узле

```zsh
pg_ctl -D "$HOME/kqz19" -l "$HOME/kqz19/server.log" start

ожидание запуска сервера.... готово
сервер запущен
```

Проверяем доступность данных

```zsh
psql -h localhost -U postgres1 -p 9042 -d fakebrownwood \
-c "SELECT count(*) FROM users;"

 count 
-------
    50
(1 строка)

```

Кол-во юзеров совпадает с primary до его "сбоя"

### Возврат узлов в прежнее состояние для последующих этапов

1. Останавливаем `pg118`

    ```zsh
    pg_ctl -D "$HOME/kqz19" stop
    ```

2. Запускаем `pg113`

    ```zsh
    pg_ctl -D "$PGDATA" start
    ```

3. Проверка на `pg118`

    ```zsh
    psql -p "$PGPORT" -d fakebrownwood -c "SELECT count(*) FROM users;"

    psql: ошибка: подключиться к серверу через сокет "/tmp/.s.PGSQL.5432" не удалось: No such file or directory
        Сервер действительно работает локально и принимает подключения через этот сокет?
    ```

## Этап 3. Повреждение файлов БД

### Фиксируем текущее состояние данных на `pg113`

```zsh
psql -p "$PGPORT" -d fakebrownwood -c "SELECT count(*) FROM users;"

 count 
-------
    50
(1 строка)
```

### Имитируем повреждение файлов БД

Останавливаем `pg113`, чтобы избежать грязного падения сервера:

Если удалить на горячую:

- будет грязный крэш
- появятся лишние ошибки
- может вмешаться WAL recovery не так, как мы ожидаем

```zsh
pg_ctl -D "$PGDATA" stop

rm -rf "$PGDATA"
ls -ld "$PGDATA"

ls: /var/db/postgres1/kqz19: No such file or directory
```

Проверка, что сервер не поднимается

```zsh
pg_ctl -D "$PGDATA" start

pg_ctl: каталог "/var/db/postgres1/kqz19" не существует
```

### Создаем новый каталог данных на основном узле

```zsh
export NEW_PGDATA=$HOME/kqz19_restored
mkdir -p "$NEW_PGDATA"
chmod 700 "$NEW_PGDATA"
```

### Копируем backup с резервного узла на основной

```zsh
mkdir -p "$HOME/basebackup_restore"
chmod 700 "$HOME/basebackup_restore"

rsync -av --delete postgres2@pg118:/var/db/postgres2/basebackup/ "$HOME/basebackup_restore/"

receiving incremental file list
./
16386.tar
16387.tar
16388.tar
backup_manifest
base.tar
pg_wal.tar

sent 141 bytes  received 49.130.592 bytes  19.652.293,20 bytes/sec
total size is 49.118.175  speedup is 1,00
```

### Распаковываем backup в новый PGDATA

```zsh
cd "$NEW_PGDATA"
tar -xf "$HOME/basebackup_restore/base.tar"
tar -xf "$HOME/basebackup_restore/pg_wal.tar"
```

### Восстанавливаем tablespace на основном узле

> Для чистого восстановления пересоздаем директории тейблспейсов, чтобы избежать перемешанных данных. В любом случае мы восстановимся на момент крайнего WAL сегмента, а не на момент непосредственно самого бэкапа

```zsh
rm -rf /var/db/postgres1/ndx81 /var/db/postgres1/beb52 /var/db/postgres1/smq7
mkdir -p /var/db/postgres1/ndx81 /var/db/postgres1/beb52 /var/db/postgres1/smq7
chmod 700 /var/db/postgres1/ndx81 /var/db/postgres1/beb52 /var/db/postgres1/smq7

rm -f "$NEW_PGDATA/pg_tblspc/16386"
rm -f "$NEW_PGDATA/pg_tblspc/16387"
rm -f "$NEW_PGDATA/pg_tblspc/16388"

ln -s /var/db/postgres1/ndx81 "$NEW_PGDATA/pg_tblspc/16386"
ln -s /var/db/postgres1/beb52 "$NEW_PGDATA/pg_tblspc/16387"
ln -s /var/db/postgres1/smq7  "$NEW_PGDATA/pg_tblspc/16388"

tar -xf "$HOME/basebackup_restore/16386.tar" -C /var/db/postgres1/ndx81
tar -xf "$HOME/basebackup_restore/16387.tar" -C /var/db/postgres1/beb52
tar -xf "$HOME/basebackup_restore/16388.tar" -C /var/db/postgres1/smq7
```

### Настраиваем recovery на основном узле

> Так как мы восстанавливаемся из backup + WAL, нужно добавить restore_command. wal_archive копируем с `pg118`

```zsh
mkdir -p /var/db/postgres1/wal_archive
chmod 700 /var/db/postgres1/wal_archive

rsync -av --delete postgres2@pg118:/var/db/postgres2/wal_archive/ /var/db/postgres1/wal_archive/

receiving incremental file list
./
000000010000000000000001
000000010000000000000002
000000010000000000000003
000000010000000000000004
000000010000000000000005
000000010000000000000005.00000028.backup
000000010000000000000006
000000010000000000000007
000000010000000000000008

sent 198 bytes  received 134.251.453 bytes  53.700.660,40 bytes/sec
total size is 134.218.066  speedup is 1,00

cat >> "$NEW_PGDATA/postgresql.auto.conf" <<'EOF'
restore_command = 'cp /var/db/postgres1/wal_archive/%f %p'
port = 9041
EOF
```

Создаем recovery.signal

```zsh
touch "$NEW_PGDATA/recovery.signal"
```

### Запускаем сервер из нового каталога

```zsh
pg_ctl -D "$NEW_PGDATA" -l "$NEW_PGDATA/server.log" start
```

### Проверяем доступность данных

```zsh
sql -p 9041 -d fakebrownwood -c "SELECT count(*) FROM users;"
 count 
-------
    50
(1 строка)
```

Как и до повреждения файлов бд

## Этап 4. Логическое повреждение данных

### В каждую таблицу базы добавить 2-3 новые строки, зафиксировать результат

- Состояние таблиц до

```zsh
[postgres1@pg113 ~]$ psql -p 9041 -d fakebrownwood -c "SELECT 
 (SELECT count(*) FROM users)  AS users_cnt, 
 (SELECT count(*) FROM orders) AS orders_cnt; ;"

 users_cnt | orders_cnt 
-----------+------------
        50 |        500
(1 строка)
```

- Добавляем новых юзеров и заказы

```zsh
[postgres1@pg113 ~]$ psql -p 9041 -d fakebrownwood -c "INSERT INTO users(username)
VALUES
  ('stage4_user_1'),
  ('stage4_user_2'),
  ('stage4_user_3');

INSERT INTO orders(user_id, amount)
SELECT id, amount
FROM (
  VALUES
    ('stage4_user_1', 1111),
    ('stage4_user_2', 2222),
    ('stage4_user_3', 3333)
) AS v(username, amount)
JOIN users u ON u.username = v.username;"
```

- Фиксируем результат

```zsh
[postgres1@pg113 ~]$ psql -p 9041 -d fakebrownwood -c "SELECT *
FROM users
WHERE username LIKE 'stage4_user_%'
ORDER BY id;

SELECT o.*
FROM orders o
JOIN users u ON u.id = o.user_id
WHERE u.username LIKE 'stage4_user_%'
ORDER BY o.id;"

 id |   username    |         created_at         
----+---------------+----------------------------
 54 | stage4_user_1 | 2026-03-29 00:31:18.003536
 55 | stage4_user_2 | 2026-03-29 00:31:18.003536
 56 | stage4_user_3 | 2026-03-29 00:31:18.003536
(3 строки)

 id  | user_id | amount |         created_at         
-----+---------+--------+----------------------------
 504 |      54 |   1111 | 2026-03-29 00:31:18.003536
 505 |      55 |   2222 | 2026-03-29 00:31:18.003536
 506 |      56 |   3333 | 2026-03-29 00:31:18.003536
(3 строки)

[postgres1@pg113 ~]$ psql -p 9041 -d fakebrownwood -c "SELECT 
 (SELECT count(*) FROM users)  AS users_cnt, 
 (SELECT count(*) FROM orders) AS orders_cnt; ;"

 users_cnt | orders_cnt 
-----------+------------
        53 |        503
(1 строка)

```

### Зафиксировать время и симулировать ошибку

- Фиксируем время сбоя на `pg113`

**На `pg113`:**

```zsh
psql -p 9041 -d fakebrownwood -c "SELECT current_timestamp;"

       current_timestamp       
-------------------------------
 2026-03-29 00:33:21.378508+03
(1 строка)
```

Будем использовать таймстемп `2026-03-29 00:33:00`

- Симулируем ошибку:

```zsh
psql -p 9041 -d fakebrownwood -c "UPDATE orders
SET amount = -1
WHERE user_id IN (
  SELECT id
  FROM users
  WHERE username LIKE 'stage4_user_%'
);

UPDATE users
SET username = 'CORRUPTED_' || id
WHERE username LIKE 'stage4_user_%';"

```

- Демонстрируем результат:

```zsh
psql -p 9041 -d fakebrownwood -c "SELECT *
FROM users
ORDER BY id DESC
LIMIT 5;

SELECT o.*
FROM orders o
JOIN users u ON u.id = o.user_id
ORDER BY o.id DESC
LIMIT 5;"

 id |   username   |         created_at         
----+--------------+----------------------------
 56 | CORRUPTED_56 | 2026-03-29 00:31:18.003536
 55 | CORRUPTED_55 | 2026-03-29 00:31:18.003536
 54 | CORRUPTED_54 | 2026-03-29 00:31:18.003536
 50 | user_50      | 2026-03-13 12:18:58.738002
 49 | user_49      | 2026-03-13 12:18:58.738002
(5 строк)

 id  | user_id | amount |         created_at         
-----+---------+--------+----------------------------
 506 |      56 |     -1 | 2026-03-29 00:31:18.003536
 505 |      55 |     -1 | 2026-03-29 00:31:18.003536
 504 |      54 |     -1 | 2026-03-29 00:31:18.003536
 500 |       3 |   8014 | 2026-03-13 12:18:58.770203
 499 |      10 |   4064 | 2026-03-13 12:18:58.770203
(5 строк)

```

### Выполняем восстановление данных

на **`pg118`**:

- Подготавливаем `PGDATA`

```zsh
rm -rf "$HOME/kqz19_pitr"
mkdir -p "$HOME/kqz19_pitr"
chmod 700 "$HOME/kqz19_pitr"

rm -rf $HOME/ndx81 $HOME/beb52 $HOME/smq7

mkdir -p $HOME/ndx81 $HOME/beb52 $HOME/smq7
chmod 700 $HOME/ndx81 $HOME/beb52 $HOME/smq7
```

- Распаковка бэкапа

```zsh
cd "$HOME/kqz19_pitr"

tar -xf $HOME/basebackup/base.tar
tar -xf $HOME/basebackup/pg_wal.tar
```

- Восстанавливаем tablespace

```zsh
rm -f "$HOME/kqz19_pitr/pg_tblspc/16386"
rm -f "$HOME/kqz19_pitr/pg_tblspc/16387"
rm -f "$HOME/kqz19_pitr/pg_tblspc/16388"

ln -s /var/db/postgres2/ndx81 "$HOME/kqz19_pitr/pg_tblspc/16386"
ln -s /var/db/postgres2/beb52 "$HOME/kqz19_pitr/pg_tblspc/16387"
ln -s /var/db/postgres2/smq7  "$HOME/kqz19_pitr/pg_tblspc/16388"

tar -xf $HOME/basebackup/16386.tar -C $HOME/ndx81
tar -xf $HOME/basebackup/16387.tar -C $HOME/beb52
tar -xf $HOME/basebackup/16388.tar -C $HOME/smq7
```

- Настраиваем recovery в postgresql.auto.conf:

```zsh
cat > "$HOME/kqz19_pitr/postgresql.auto.conf" <<'EOF'
restore_command = 'cp /var/db/postgres2/wal_archive/%f %p'
recovery_target_time = '2026-03-29 00:33:00'
recovery_target_action = 'pause'
port = 9042
EOF
```

- Создаем `.signal` файл

```zsh
touch "$HOME/kqz19_pitr/recovery.signal"
```

- Запускаем кластер

```zsh
pg_ctl -D "$HOME/kqz19_pitr" -l "$HOME/kqz19_pitr/server.log" start
```

- Проверяем PITR:

```zsh
psql -h localhost -p 9042 -U postgres1 -d fakebrownwood -c "SELECT 
 (SELECT count(*) FROM users)  AS users_cnt, 
 (SELECT count(*) FROM orders) AS orders_cnt; ;"

 users_cnt | orders_cnt 
-----------+------------
        53 |        503
(1 строка)

psql -h localhost -p 9042 -U postgres1 -d fakebrownwood -c "SELECT *
FROM users
ORDER BY id DESC
LIMIT 5;

SELECT o.*
FROM orders o
JOIN users u ON u.id = o.user_id
ORDER BY o.id DESC
LIMIT 5;"

 id |   username    |         created_at         
----+---------------+----------------------------
 56 | stage4_user_3 | 2026-03-29 00:31:18.003536
 55 | stage4_user_2 | 2026-03-29 00:31:18.003536
 54 | stage4_user_1 | 2026-03-29 00:31:18.003536
 50 | user_50       | 2026-03-13 12:18:58.738002
 49 | user_49       | 2026-03-13 12:18:58.738002
(5 строк)

 id  | user_id | amount |         created_at         
-----+---------+--------+----------------------------
 506 |      56 |   3333 | 2026-03-29 00:31:18.003536
 505 |      55 |   2222 | 2026-03-29 00:31:18.003536
 504 |      54 |   1111 | 2026-03-29 00:31:18.003536
 500 |       3 |   8014 | 2026-03-13 12:18:58.770203
 499 |      10 |   4064 | 2026-03-13 12:18:58.770203
(5 строк)

```

Видим полное соответствие состоянию до лоигческого повреждения

- Делаем `pg_dump` и переносим на `pg113`:

```zsh
pg_dump -h localhost -p 9042 -U postgres1 -d fakebrownwood > recovered_dump.sql

scp recovered_dump.sql postgres1@pg113:~
```

### Выполняем восстановление на основном узле

- Убиваем активные подключения

```zsh
psql -p 9041 -d postgres -c "
SELECT pg_terminate_backend(pid)
FROM pg_stat_activity
WHERE datname = 'fakebrownwood';
"
```

- Пересоздаем БД

```zsh
psql -p 9041 -d postgres -c "DROP DATABASE fakebrownwood;"
psql -p 9041 -d postgres -c "CREATE DATABASE fakebrownwood;"
```

- Применяем dump

```zsh
psql -p 9041 -d fakebrownwood -f recovered_dump.sql
```

- Проверяем данные

```zsh
psql -p 9041 -d fakebrownwood -c "SELECT *
FROM users
ORDER BY id DESC
LIMIT 5;

SELECT o.*
FROM orders o
JOIN users u ON u.id = o.user_id
ORDER BY o.id DESC
LIMIT 5;"

 id |   username    |         created_at         
----+---------------+----------------------------
 56 | stage4_user_3 | 2026-03-29 00:31:18.003536
 55 | stage4_user_2 | 2026-03-29 00:31:18.003536
 54 | stage4_user_1 | 2026-03-29 00:31:18.003536
 50 | user_50       | 2026-03-13 12:18:58.738002
 49 | user_49       | 2026-03-13 12:18:58.738002
(5 строк)

 id  | user_id | amount |         created_at         
-----+---------+--------+----------------------------
 506 |      56 |   3333 | 2026-03-29 00:31:18.003536
 505 |      55 |   2222 | 2026-03-29 00:31:18.003536
 504 |      54 |   1111 | 2026-03-29 00:31:18.003536
 500 |       3 |   8014 | 2026-03-13 12:18:58.770203
 499 |      10 |   4064 | 2026-03-13 12:18:58.770203
(5 строк)

```

Данные успешно восстановлены
