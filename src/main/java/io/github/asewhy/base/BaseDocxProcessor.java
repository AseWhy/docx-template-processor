package io.github.asewhy.base;

import io.github.asewhy.exceptions.ProcessorException;
import io.github.asewhy.interfaces.DocxProcessor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.ContentAccessor;
import org.docx4j.wml.R;

import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@SuppressWarnings("unchecked")
@Log4j2
@Getter
@Setter
public abstract class BaseDocxProcessor implements DocxProcessor {
    protected boolean loggable = false;

    /**
     * Получить все узлы нужного типа элемента в строке
     *
     * @param obj объект
     * @param toSearch класс для поиска
     * @param <T> тип для поиска
     * @return список элементов
     */
    protected <T> List<T> getAllElementFromObject(Object obj, Class<T> toSearch) {
        var stack = new ArrayList<T>();
        getAllElementFromObject(stack, obj, toSearch, Set.of());
        return stack;
    }

    /**
     * Получить все узлы нужного типа элемента в строке
     *
     * @param obj объект
     * @param toSearch класс для поиска
     * @param <T> тип для поиска
     * @return список элементов
     */
    protected <T> List<T> getAllElementFromObject(Object obj, Class<T> toSearch, Set<Object> excludes) {
        var stack = new ArrayList<T>();
        getAllElementFromObject(stack, obj, toSearch, excludes);
        return stack;
    }

    /**
     * Получить все узлы нужного типа элемента в строке
     *
     * @param stack стек уже найденных элементов
     * @param obj объект
     * @param toSearch класс для поиска
     * @param <T> тип для поиска
     */
    protected <T> void getAllElementFromObject(List<T> stack, Object obj, Class<T> toSearch, Set<Object> excludes) {
        if (obj instanceof JAXBElement<?>) {
            obj = ((JAXBElement<Object>) obj).getValue();
        }

        if(excludes.contains(obj)) {
            return;
        }

        if (obj.getClass().equals(toSearch)) {
            stack.add((T) obj);
        } else if (obj instanceof ContentAccessor) {
            var accessor = (ContentAccessor) obj;

            for(var current: accessor.getContent()) {
                if (current == null) {
                    continue;
                }

                getAllElementFromObject(stack, current, toSearch, excludes);
            }
        }
    }

    public void optimize(WordprocessingMLPackage template) {
        var rowRuns = getAllElementFromObject(template.getMainDocumentPart(), R.class);
    }

    @Override
    public void doProcess(WordprocessingMLPackage template) throws ProcessorException {
        long start = System.currentTimeMillis();

        doProcessLoggable(template);

        if(loggable) {
            log.info("Template processing complete. Took " + (System.currentTimeMillis() - start) + "ms.");
        }
    }

    @Override
    public Collection<String> doValidate(WordprocessingMLPackage template) throws ProcessorException {
        long start = System.currentTimeMillis();

        var result = doValidateLoggable(template);

        if(loggable) {
            log.info("Template processing complete. Took " + (System.currentTimeMillis() - start) + "ms.");
        }

        return result;
    }

    /**
     * Если нужно, чтобы по завершении обработки выводилось количество времени затраченное на обработку,
     * то нужно переопределить этот метод
     *
     * @param template шаблон для обработки
     * @throws ProcessorException если при обработке произошла какая-то хрень
     */
    protected void doProcessLoggable(WordprocessingMLPackage template) throws ProcessorException {

    }

    /**
     * Если нужно, чтобы по завершении проверки выводилось количество времени затраченное на проверку,
     * то нужно переопределить этот метод
     *
     * @param template шаблон для проверки
     * @throws ProcessorException если при обработке произошла какая-то хрень
     */
    protected List<String> doValidateLoggable(WordprocessingMLPackage template) throws ProcessorException {
        return List.of();
    }
}
