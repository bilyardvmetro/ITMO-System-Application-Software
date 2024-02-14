package Modules;


public class CommandHandler {
    private CollectionService collectionService;

    public CommandHandler() {
        this.collectionService = new CollectionService();
    }

    public void help(){
        System.out.println(
                """
                        Список доступных команд:
                        ================================================================================================
                        help - справка по командам
                        info - вывод данных о коллекции (тип, дата инициализации, количество элементов)
                        show - вывести все элементы коллекции
                        add <el> - добавить элемент в коллекцию
                        update <id> <el> - обновить id элемента на заданный
                        remove_by_id <id> - удалить элемент по id
                        clear - очистить всю коллекцию
                        save - сохранить коллекцию
                        execute_script <path> - исполнить скрипт
                        exit - закрыть приложение без сохранения данных
                        remove_greater <el> - удалить из коллекции все элементы, превышающие данный
                        reorder - отсортировать коллекцию в порядке, обратном нынешнему
                        history - вывести последние 7 команд
                        remove_all_by_type <type> - удалить из коллекции все элементы, значение поля которых равно заданному
                        count_greater_than_engine_power <engine_power> - вывести количество элементов коллекции, мощность которых выше заданной
                        filter_starts_with_name <name> - вывести все элементы, значение поля name которых начинается с заданной подстроки
                        ================================================================================================
                        """
        );
    }

    public void info(){}

    public void show(){}

    public void add(){
        collectionService.addElement();
    }

    public void update(){}

    public void removeById(){}

    public void clear(){}

    public void save(){}

    public void executeScript(){}

    public void exit(){
        System.exit(0);
    }

    public void removeGreater(){}

    public void reorder(){}

    public void history(){}

    public void removeAllByType(){}

    public void countGreaterThanEnginePower(){}

    public void filterStartsWithName(){}

}
