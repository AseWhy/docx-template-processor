package io.github.asewhy.base;

import io.github.asewhy.ProcessorArgumentResolver;
import io.github.asewhy.ProcessorTypeProvider;
import io.github.asewhy.exceptions.ProcessorException;
import io.github.asewhy.support.IterableBagData;
import org.docx4j.XmlUtils;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.Text;
import org.docx4j.wml.Tr;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public abstract class BaseSequenceTagProcessor extends BaseDocxProcessor {
    /**
     * Получить значение по тегу key
     *
     * @param key тег
     * @return значение тега
     */
    protected abstract String getTag(String key);

    /**
     * Получить значение по тегу key и ключу index
     *
     * @param key тег
     * @param index индекс
     * @param subKey подключ значения полученного по индексу
     * @return значение тега
     */
    protected abstract String getTag(String key, Integer index, String subKey);

    /**
     * Получить набор тегов, которые присутствуют в маппере тегов
     *
     * @return набор тегов, которые присутствуют в маппере тегов
     */
    protected abstract List<String> getSupportTagNames(String subspace);

    /**
     * Получить значение числа строк в таблицу
     *
     * @param key ключ по которому нужно получить число строк в таблице
     * @return число строк в таблице, или -1 если значение по ключу не массив
     */
    protected abstract Integer getTableRowCount(String key);

    /**
     * Проверить, является ли бинд тега итерируемым
     *
     * @param key тег по которому нужно проверить
     * @return true если является
     */
    protected abstract Boolean isIterableTag(String key);

    /**
     * Проверить, является ли это строка итерируемой
     *
     * @param nodes список текстовых узлов строки
     * @param supportTagNames поддерживаемые названия тегов
     * @return не null, если является
     */
    private IterableBagData isIterableRowCheck(List<Text> nodes, List<String> supportTagNames) {
        for (var node : nodes) {
            var textContent = node.getValue();

            for (var tag : supportTagNames) {
                //
                // Если текущий текстовый узел содержит тег
                //
                if (textContent.contains(tag)) {
                    if(isIterableTag(tag)) {
                        return new IterableBagData(0, tag);
                    }
                }
            }
        }

        return null;
    }

    /**
     * Проверить, является ли это строка итерируемой
     *
     * @param nodes список текстовых узлов строки
     * @param supportTagNames поддерживаемые названия тегов
     * @return 0 или больше если является (количество необходимых повторений)
     */
    private IterableBagData isIterableRow(List<Text> nodes, List<String> supportTagNames) {
        for (var node : nodes) {
            var textContent = node.getValue();

            for (var tag : supportTagNames) {
                //
                // Если текущий текстовый узел содержит тег
                //
                if (textContent.contains(tag)) {
                    var count = getTableRowCount(tag);

                    if(count > -1) {
                        return new IterableBagData(count, tag);
                    }
                }
            }
        }

        return null;
    }

    /**
     * Заменить теги в найденных текстовых узлах
     *
     * @param textNodes список текстовых узлов для замены
     * @param supportTagNames поддерживаемые теги
     * @throws ProcessorException если произошла ошибка
     */
    private void replaceTags(List<Text> textNodes, List<String> supportTagNames) throws ProcessorException {
        for(var node: textNodes) {
            var textContent = node.getValue();

            try {
                for(var tag: supportTagNames) {
                    if(textContent.contains(tag)) {
                        textContent = textContent.replaceAll(tag, getTag(tag));
                    }
                }
            } catch (RuntimeException e) {
                throw new ProcessorException(e);
            }

            node.setValue(textContent);
        }
    }

    /**
     * Заменить теги в найденных текстовых узлах
     *
     * @param textNodes список текстовых узлов для замены
     * @param supportTagNames поддерживаемые теги
     * @throws ProcessorException если произошла ошибка
     */
    private void replaceTags(List<Text> textNodes, List<String> supportTagNames, Integer dataIndex, String primaryTag) throws ProcessorException {
        for(var node: textNodes) {
            var textContent = node.getValue();

            try {
                for(var tag: supportTagNames) {
                    if(textContent.contains(tag)) {
                        textContent = textContent.replaceAll(tag, getTag(primaryTag, dataIndex, tag));
                    }
                }
            } catch (RuntimeException e) {
                throw new ProcessorException(e);
            }

            node.setValue(textContent);
        }
    }

    @Override
    public void doProcessLoggable(@NotNull WordprocessingMLPackage template) throws ProcessorException {
        var tableNodes = getAllElementFromObject(template.getMainDocumentPart(), Tbl.class);
        var textNodes = getAllElementFromObject(template.getMainDocumentPart(), Text.class, new HashSet<>(tableNodes));
        var supportTagNames = getSupportTagNames(ProcessorTypeProvider.MAIN_SUBSPACE);

        replaceTags(textNodes, supportTagNames);

        for(var table: tableNodes) {
            var nodes = table.getContent();
            var rowNodes = getAllElementFromObject(table, Tr.class);

            for(var row: rowNodes) {
                var rowTextNodes = getAllElementFromObject(row, Text.class);
                var rowIterablesData = isIterableRow(rowTextNodes, supportTagNames);

                if(rowIterablesData != null) {
                    var index = nodes.indexOf(row);
                    var currentSubspace = getSupportTagNames(rowIterablesData.getIterablesPrimaryTag());

                    nodes.remove(index);

                    for(var i = 0; i < rowIterablesData.getIterablesCount(); i++) {
                        var newRow = XmlUtils.deepCopy(row);
                        var newRowTextNodes = getAllElementFromObject(newRow, Text.class);
                        replaceTags(newRowTextNodes, currentSubspace, i, rowIterablesData.getIterablesPrimaryTag());
                        nodes.add(index++, newRow);
                    }
                } else {
                    replaceTags(rowTextNodes, supportTagNames);
                }
            }
        }
    }

    @Override
    public Collection<String> doValidate(@NotNull WordprocessingMLPackage template) {
        var foundTags = new HashSet<String>();
        var tableNodes = getAllElementFromObject(template.getMainDocumentPart(), Tbl.class);
        var textNodes = getAllElementFromObject(template.getMainDocumentPart(), Text.class, new HashSet<>(tableNodes));
        var supportTagNames = getSupportTagNames(ProcessorTypeProvider.MAIN_SUBSPACE);

        for(var node: textNodes) {
            var textContent = node.getValue();

            for(var tag: supportTagNames) {
                if(textContent.contains(tag)) {
                    foundTags.add(tag);
                }
            }
        }

        for(var table: tableNodes) {
            var nodes = table.getContent();
            var rowNodes = getAllElementFromObject(table, Tr.class);

            for(var row: rowNodes) {
                var rowTextNodes = getAllElementFromObject(row, Text.class);
                var rowIterablesData = isIterableRowCheck(rowTextNodes, supportTagNames);

                if(rowIterablesData != null) {
                    var currentSubspace = getSupportTagNames(rowIterablesData.getIterablesPrimaryTag());

                    for(var node: rowTextNodes) {
                        var textContent = node.getValue();

                        for(var tag: currentSubspace) {
                            if(textContent.contains(tag)) {
                                foundTags.add(ProcessorArgumentResolver.getSubPropertyIndex(tag, rowIterablesData.getIterablesPrimaryTag()));
                            }
                        }
                    }
                } else {
                    for(var node: rowTextNodes) {
                        var textContent = node.getValue();

                        for(var tag: supportTagNames) {
                            if(textContent.contains(tag)) {
                                foundTags.add(tag);
                            }
                        }
                    }
                }
            }
        }

        return foundTags;
    }
}
