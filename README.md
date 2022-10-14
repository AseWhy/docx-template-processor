# Docx template processor

Небольшой модуль для обработки шаблонов docx. Обработка происходит без префиксов и суффиксов, теги заменяются "Как есть".
Для начала работы с модулем, необходимо создать экземпляр одного из наследников `BaseDocxProcessor`.

## Базовое использование

Обработчик работает по заранее составленной карте биндингов, которые хранятся в классе `ProcessorTypeProvider`. Биндинги 
нужны для быстрого доступа к полям, из которых можно получить значения тегов. На пример можно сделать биндинг для макроса
someTag на SomeClass#someTag. Для того чтобы предоставить данные, биндинги на которые уже присутствуют в обработчике, 
можно использовать `ProcessorDataProvider`. Или можно просто создать экземпляр SequenceResolveTagProcessor, который требует
только `ProcessorTypeProvider` который может быть одиночкой, т.к. его состояние статическое.

```java
import io.github.asewhy.ProcessorTypeProvider;
import io.github.asewhy.processors.SequenceResolveTagProcessor;

class SomeClass {
    public static ProcessorTypeProvider typeProvider = new ProcessorTypeProvider();
    
    static {
        typeProvider
            .provide(Object.class)
                .bind("hashCode", "hashCode", "Хэш код объекта")
            .build();
    }

    public static void main(String[] args) {
        var processor = new SequenceResolveTagProcessor();

        processor.provide(Object.class, Object::new);
        
        // Use processor next
    }
}
```

В примере выше обработчику был предоставлен биндинг на метод объекта hashCode для макроса hashCode. Помимо методов может использоваться и поле объекта
или метод одного из интерфейсов которые объект реализует. Кроме того если поставляемый тип является базовым классом или интерфейсом, то при обработке
его наследники могут использовать типа поставляемые этим интерфейсом.

## Связи

При поставлении данных в обработчике можно использовать связи.

```java
import io.github.asewhy.ProcessorTypeProvider;
import io.github.asewhy.processors.SequenceResolveTagProcessor;

class SomeClass {
    public static ProcessorTypeProvider typeProvider = new ProcessorTypeProvider();
    
    static {
        typeProvider
            .provide(Object.class)
                .bind("hashCode", "hashCode", "Хэш код объекта")
            .build()
        .provide(SomeOtherClass.class)
            .bind("someTag", "someData", "Какие-то данные")
        .build();
    }

    public static void main(String[] args) {
        var processor = new SequenceResolveTagProcessor();

        processor.provide(SomeOtherClass.class, SomeOtherClass::new);
        processor.provide(Object.class, () -> processor.resolve(SomeOtherClass.class).getRelatedData());
        
        // Use processor next
    }
    
    class SomeOtherClass {
        private int someData;
        
        public Object getRelatedData() {
            return new Object();
        }
    }
}
```

В примере выше показано как использовать связи. Таким образом для получения экземпляра `Object` будет использован уже имеющийся
экземпляр `SomeOtherClass` или, если его ещё нет в кеше, он будет создан заново, и из него будет получен экземпляр `Object`
путем вызова `getRelatedData`.

## Получение информации о биндингах

Из поставщика типов можно получить информацию о биндингах. Это может быть полезным, например если нужно рассказать пользователю какие макросы
он может использовать в своем шаблоне.

```java
import io.github.asewhy.ProcessorTypeProvider;
import io.github.asewhy.processors.SequenceResolveTagProcessor;

class SomeClass {
    public static ProcessorTypeProvider typeProvider = new ProcessorTypeProvider();
    
    static {
        typeProvider
            .provide(Object.class)
                .bind("hashCode", "hashCode", "Хэш код объекта")
            .build()
        .provide(SomeOtherClass.class)
            .bind("someTag", "someData", "Какие-то данные")
        .build();
    }

    public static void main(String[] args) {
        // Получаем информацию о биндингах
        System.out.println(typeProvider.getDescriptionMap());
    }
    
    class SomeOtherClass {
        private int someData;
        
        public Object getRelatedData() {
            return new Object();
        }
    }
}
```