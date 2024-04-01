package Modules;


import CollectionObject.VehicleModel;
import CollectionObject.VehicleType;
import Exceptions.NonExistingElementException;
import Network.Response;

import java.util.LinkedList;


public class CommandHandler {
    private CollectionService collectionService;
    private static CSVProvider csvProvider;
    private static LinkedList<String> commandHistory = new LinkedList<>();

    public CommandHandler() {
        this.collectionService = new CollectionService();
        csvProvider = new CSVProvider(CSVProvider.COLLECTION_PATH);
    }

    public Response help(String strArgument, VehicleModel objArgument){
        if (!strArgument.isBlank() && objArgument != null){
            return new Response("Неверные аргументы команды", "" );

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
                """
            ;
            return new Response(message, "");
        }
    }

    public Response info(String strArgument, VehicleModel objArgument){
        if (!strArgument.isBlank() && objArgument != null){
            return new Response("Неверные аргументы команды", "" );

        } else {
            var message = collectionService.info();
            return new Response(message, "");
        }
    }

    public Response show(String strArgument, VehicleModel objArgument){
        if (!strArgument.isBlank() && objArgument != null){
            return new Response("Неверные аргументы команды", "" );

        } else {
            var collection = collectionService.show();

            if (collection.isEmpty()){
                return new Response("В коллекции пока нету ни одного элемента", "");
            } else{
                return new Response("Коллекция успешно распечатана", collection.toString());
            }
        }
    }

    public Response add(String strArgument, VehicleModel objArgument){
        if (!strArgument.isBlank() && objArgument == null){
            return new Response("Неверные аргументы команды", "" );

        } else {
            var collection = collectionService.add(objArgument);
            return new Response("Элемент успешно добавлен", collection.toString());
        }
    }

    public Response update(String strArgument, VehicleModel objArgument){
        if (strArgument.isBlank() && objArgument == null){
            return new Response("Неверные аргументы команды", "" );

        } else {
            try {
                long current_id = Long.parseLong(strArgument);

                if (current_id > 0){
                    var collection = collectionService.update(current_id, objArgument);
                    return new Response("элемент c id " + current_id + " успешно обновлён", collection.toString());

                } else {
                    return new Response("id не может быть отрицательным", "" );
                }

            } catch (NumberFormatException e){
                return new Response("Неверный формат аргументов", "" );
            } catch (NonExistingElementException e) {
                return new Response(e.getMessage(), "");
            }
        }
    }

    public Response removeById(String strArgument, VehicleModel objArgument){
        if (strArgument.isBlank() && objArgument != null){
            return new Response("Неверные аргументы команды", "" );

        } else {
            try {
                long id = Long.parseLong(strArgument);

                if (id > 0){
                    var collection = collectionService.removeById(id);
                    return new Response("Элемент с id " + id + " успешно удалён", collection.toString());
                } else {
                    return new Response("id не может быть отрицательным", "" );
                }

            } catch (NumberFormatException e){
                return new Response("Неверный формат аргументов", "" );
            } catch (NonExistingElementException e) {
                return new Response(e.getMessage(), "");
            }
        }
    }

    public Response clear(String strArgument, VehicleModel objArgument){
        if (!strArgument.isBlank() && objArgument != null){
            return new Response("Неверные аргументы команды", "" );

        } else {
            var collection = collectionService.clear();
            return new Response("коллекция успешно очищена", collection.toString());
        }
    }

    public static void save(){
        csvProvider.save(CollectionService.collection);
    }

    public Response removeGreater(String strArgument, VehicleModel objArgument){
        if (strArgument.isBlank() && objArgument != null){
            return new Response("Неверные аргументы команды", "" );

        } else {
            try {
                long startId = Long.parseLong(strArgument) + 1;

                if (startId > 0) {
                    var collection = collectionService.removeGreater(startId);
                    return new Response("элементы успешно удалены", collection.toString());

                } else {
                    return new Response("id не может быть отрицательным", "");
                }

            } catch (NumberFormatException e){
                return new Response("Неверный формат аргументов", "" );
            } catch (NonExistingElementException e) {
                return new Response(e.getMessage(), "");
            }
        }
    }

    public Response reorder(String strArgument, VehicleModel objArgument){
        if (!strArgument.isBlank() && objArgument != null){
            return new Response("Неверные аргументы команды", "" );

        } else {
            var collection = collectionService.reorder();
            return new Response("элементы успешно отсортированы по убыванию", collection.toString());
        }
    }

    public Response history(String strArgument, VehicleModel objArgument){
        StringBuilder historyList = new StringBuilder();

        if (!strArgument.isBlank() && objArgument != null){
            return new Response("Неверные аргументы команды", "" );

        } else {
            for (String command : commandHistory) {
                historyList.append(command).append("\n");
            }
        }
        return new Response("Последние 7 команд, введённые пользователем: \n" + historyList, "" );
    }

    public Response removeAllByType(String strArgument, VehicleModel objArgument){
        if (strArgument.isBlank() && objArgument != null){
            return new Response("Неверные аргументы команды", "" );

        } else {
            try {
                VehicleType type = VehicleType.valueOf(strArgument.toUpperCase());
                var collection = collectionService.removeAllByType(type);
                return new Response("Транспортные средства с типом " + type + " успешно удалены", collection.toString());

            } catch (IllegalArgumentException e){
                return new Response("Такого типа транспортных средств не существует", "" );
            } catch (NonExistingElementException e) {
                return new Response(e.getMessage(), "");
            }
        }
    }

    public Response countGreaterThanEnginePower(String strArgument, VehicleModel objArgument){
        if (strArgument.isBlank() && objArgument != null){
            return new Response("Неверные аргументы команды", "" );

        } else {
            try {
                double enginePower = Double.parseDouble(strArgument);
                if (enginePower > 0) {
                    var count = collectionService.countGreaterThanEnginePower(enginePower);
                    return new Response("Количество ТС с мощностью больше " + enginePower + " " + count, "" );

                } else {
                    return new Response("Мощность двигателя не может быть отрицательной", "" );
                }

            } catch (NumberFormatException e){
                return new Response("Неверный формат аргументов", "" );
            }
        }
    }

    public Response filterStartsWithName(String strArgument, VehicleModel objArgument){
        if (strArgument.isBlank() && objArgument != null){
            return new Response("Неверные аргументы команды", "" );

        } else {
            try {
                var collection = collectionService.filterStartsWithName(strArgument);
                return new Response("Коллекция отсортирована по именам ТС", collection.toString());

            } catch (NonExistingElementException e) {
                return new Response(e.getMessage(), "");
            }

        }
    }


    public static void addCommand(String command){
        if (commandHistory.size() == 7){
            commandHistory.removeFirst();
        }
        commandHistory.addLast(command);
    }
}
