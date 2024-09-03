package Modules;


import CollectionObject.Vehicle;
import CollectionObject.VehicleModel;
import CollectionObject.VehicleType;
import Exceptions.DBProviderException;
import Exceptions.NonExistingElementException;
import Network.Response;
import Network.User;

import java.util.LinkedList;
import java.util.Stack;


public class CommandHandler {
    private CollectionService collectionService;
    private static LinkedList<String> commandHistory = new LinkedList<>();

    public CommandHandler() {
        this.collectionService = new CollectionService();
    }

    public Response help(User user, String strArgument, VehicleModel objArgument) {
        if (!strArgument.isBlank() || objArgument != null) {
            return new Response("Неверные аргументы команды", "");

        } else {
            String message =
                    """
                            Список доступных команд:
                            ================================================================================================
                            help - справка по командам
                            info - вывод данных о коллекции (тип, дата инициализации, количество элементов)
                            show - вывести все элементы коллекции
                            add <el> - добавить элемент в коллекцию
                            update <id> <el> - обновить id элемента на заданный
                            removeById <id> - удалить элемент по id
                            clear - очистить всю коллекцию
                            executeScript <path> - исполнить скрипт
                            exit - завершить работу клиентского приложения
                            removeGreater <id> - удалить из коллекции все элементы, превышающие данный по id
                            reorder - отсортировать коллекцию в обратном порядке
                            history - вывести последние 7 команд
                            removeAllByType <type> - удалить из коллекции все элементы, значение тип которых соответствует заданному
                            countGreaterThanEnginePower <engine_power> - вывести количество элементов коллекции, мощность которых выше заданной
                            filterStartsWithName <name> - вывести все элементы, имя которых начинается с заданной подстроки
                            ================================================================================================
                            """;
            return new Response(message, "");
        }
    }

    public Response info(User user, String strArgument, VehicleModel objArgument) {
        if (!strArgument.isBlank() || objArgument != null) {
            return new Response("Неверные аргументы команды", "");

        } else {
            var message = collectionService.info();
            return new Response(message, "");
        }
    }

    public synchronized Response show(User user, String strArgument, VehicleModel objArgument) {
        if (!strArgument.isBlank() || objArgument != null) {
            return new Response("Неверные аргументы команды", "");

        } else {
            var collection = collectionService.show();

            if (collection.isEmpty()) {
                return new Response("В коллекции пока нету ни одного элемента", "");
            } else {
                return new Response("Коллекция успешно распечатана", collection.toString());
            }
        }
    }

    public synchronized Response add(User user, String strArgument, VehicleModel objArgument) {
        if (!strArgument.isBlank() && objArgument == null) {
            return new Response("Неверные аргументы команды", "");

        } else {
            Stack<Vehicle> collection;
            try {
                collection = collectionService.add(objArgument);
            } catch (DBProviderException e) {
                return new Response(e.getMessage(), "");
            }
            return new Response("Элемент успешно добавлен", collection.toString());
        }
    }

    public synchronized Response update(User user, String strArgument, VehicleModel objArgument) {
        if (strArgument.isBlank() && objArgument == null) {
            return new Response("Неверные аргументы команды", "");

        } else {
            try {
                long current_id = Long.parseLong(strArgument);

                if (current_id > 0) {

                    if (CollectionService.collection.stream().anyMatch(vehicle -> vehicle.getId() == current_id)) {

                        if (CollectionService.collection.stream().anyMatch(vehicle -> vehicle.getId() == current_id
                                && vehicle.getCreator().equals(user.getUsername()))) {

                            var collection = collectionService.update(user, current_id, objArgument);
                            return new Response("элемент c id " + current_id + " успешно обновлён", collection.toString());
                        }
                        return new Response("Вы не можете изменить этот объект", "");

                    }
                    return new Response("Элемента с таким id не существует", "");

                } else {
                    return new Response("id не может быть отрицательным", "");
                }

            } catch (NumberFormatException e) {
                return new Response("Неверный формат аргументов", "");
            } catch (DBProviderException e) {
                return new Response(e.getMessage(), "");
            }
        }
    }

    public synchronized Response removeById(User user, String strArgument, VehicleModel objArgument) {
        if (strArgument.isBlank() || objArgument != null) {
            return new Response("Неверные аргументы команды", "");

        } else {
            try {
                long id = Long.parseLong(strArgument);

                if (id > 0) {
                    if (CollectionService.collection.stream().anyMatch(vehicle -> vehicle.getId() == id)) {
                        if (CollectionService.collection.stream().anyMatch(vehicle -> vehicle.getId() == id
                                && vehicle.getCreator().equals(user.getUsername()))) {

                            var collection = collectionService.removeById(user, id);
                            return new Response("Элемент с id " + id + " успешно удалён", collection.toString());
                        }
                        return new Response("Вы не можете удалить этот объект", "");
                    }
                    return new Response("Элемента с таким id не существует", "");
                }
                return new Response("id не может быть отрицательным", "");

            } catch (NumberFormatException e) {
                return new Response("Неверный формат аргументов", "");
            } catch (DBProviderException e) {
                return new Response(e.getMessage(), "");
            }
        }
    }

    public synchronized Response clear(User user, String strArgument, VehicleModel objArgument) {
        if (!strArgument.isBlank() || objArgument != null) {
            return new Response("Неверные аргументы команды", "");

        } else {
            Stack<Vehicle> collection;
            try {
                collection = collectionService.clear(user);
            } catch (DBProviderException e) {
                return new Response(e.getMessage(), "");
            }
            return new Response("коллекция успешно очищена", collection.toString());
        }
    }

    public synchronized Response removeGreater(User user, String strArgument, VehicleModel objArgument) {
        if (strArgument.isBlank() || objArgument != null) {
            return new Response("Неверные аргументы команды", "");

        } else {
            try {
                long startId = Long.parseLong(strArgument) + 1;

                if (startId > 0) {
                    var collection = collectionService.removeGreater(user, startId);
                    return new Response("элементы успешно удалены", collection.toString());

                } else {
                    return new Response("id не может быть отрицательным", "");
                }

            } catch (NumberFormatException e) {
                return new Response("Неверный формат аргументов", "");
            } catch (NonExistingElementException | DBProviderException e) {
                return new Response(e.getMessage(), "");
            }
        }
    }

    public synchronized Response reorder(User user, String strArgument, VehicleModel objArgument) {
        if (!strArgument.isBlank() || objArgument != null) {
            return new Response("Неверные аргументы команды", "");

        } else {
            var collection = collectionService.reorder();
            return new Response("элементы успешно отсортированы по убыванию", collection.toString());
        }
    }

    public Response history(User user, String strArgument, VehicleModel objArgument) {
        StringBuilder historyList = new StringBuilder();

        if (!strArgument.isBlank() || objArgument != null) {
            return new Response("Неверные аргументы команды", "");

        } else {
            for (String command : commandHistory) {
                historyList.append(command).append("\n");
            }
        }
        return new Response("Последние 7 команд, введённые пользователем: \n" + historyList, "");
    }

    public synchronized Response removeAllByType(User user, String strArgument, VehicleModel objArgument) {
        if (strArgument.isBlank() || objArgument != null) {
            return new Response("Неверные аргументы команды", "");

        } else {
            try {
                VehicleType type = VehicleType.valueOf(strArgument.toUpperCase());
                var collection = collectionService.removeAllByType(user, type);
                return new Response("Транспортные средства с типом " + type + " успешно удалены", collection.toString());

            } catch (IllegalArgumentException e) {
                return new Response("Такого типа транспортных средств не существует", "");
            } catch (NonExistingElementException | DBProviderException e) {
                return new Response(e.getMessage(), "");
            }
        }
    }

    public synchronized Response countGreaterThanEnginePower(User user, String strArgument, VehicleModel objArgument) {
        if (strArgument.isBlank() || objArgument != null) {
            return new Response("Неверные аргументы команды", "");

        } else {
            try {
                double enginePower = Double.parseDouble(strArgument);
                if (enginePower > 0) {
                    var count = collectionService.countGreaterThanEnginePower(enginePower);
                    return new Response("Количество ТС с мощностью больше " + enginePower + ":   " + count, "");

                } else {
                    return new Response("Мощность двигателя не может быть отрицательной", "");
                }

            } catch (NumberFormatException e) {
                return new Response("Неверный формат аргументов", "");
            }
        }
    }

    public synchronized Response filterStartsWithName(User user, String strArgument, VehicleModel objArgument) {
        if (strArgument.isBlank() || objArgument != null) {
            return new Response("Неверные аргументы команды", "");

        } else {
            try {
                var collection = collectionService.filterStartsWithName(strArgument);
                return new Response("Коллекция отсортирована по именам ТС", collection.toString());

            } catch (NonExistingElementException e) {
                return new Response(e.getMessage(), "");
            }

        }
    }


    public synchronized static void addCommand(String command) {
        if (commandHistory.size() == 7) {
            commandHistory.removeFirst();
        }
        commandHistory.addLast(command);
    }
}