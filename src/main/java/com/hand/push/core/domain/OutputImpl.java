package com.hand.push.core.domain;

import com.hand.push.dto.PushEntry;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

/**
 * Created with IntelliJ IDEA.
 * User: emerson
 * Date: 12/23/13
 * Time: 2:54 PM
 */
public class OutputImpl implements Output{
    private final Vector<ErrorEntry> errors;
    private final Vector<PushEntry> success;

    public OutputImpl() {
        errors = new Vector<ErrorEntry>();
        success = new Vector<PushEntry>();
    }

    @Override
    public Output addErrorEntries(List<ErrorEntry> entries) {
        for (ErrorEntry error : entries) {
            yieldErrorEntries(error);
        }
        return this;
    }

    @Override
    public Output addErrorEntry(ErrorEntry entry) {
        //TODO 重构add方式
        yieldErrorEntries(entry);
        return this;
    }

    @Override
    public List<ErrorEntry> getErrors() {
        return Collections.unmodifiableList(errors);
    }

    @Override
    public Output addSuccessEntry(PushEntry entry) {
        success.add(entry);
        return this;
    }

    @Override
    public Output addSuccessEntries(List<PushEntry> entries) {
        success.addAll(entries);
        return this;
    }

    @Override
    public List<PushEntry> getSuccesses() {
        return Collections.unmodifiableList(success);
    }

    private Output yieldErrorEntries(ErrorEntry entry) {

        if (entry.getData() != null && (!entry.getData().isEmpty()) && entry.getCausedBy()!= null){
            //1.根据cause类型，查看当前存储的错误信息中有没有相同原因的
            ErrorEntry sameCause = null;
            for (ErrorEntry stored : errors) {
                if (stored.getCausedBy().getClass().equals(entry.getCausedBy().getClass())) {
                    sameCause = stored;
                    break;
                }
            }

            //说明没有相同原因的
            if (sameCause == null) {
                errors.add(entry);
            } else {
                //存在相同，合并
                List<PushEntry> newList = new LinkedList<PushEntry>(sameCause.getData());
                newList.addAll(entry.getData());
                ErrorEntry yieldEntry = new ErrorEntry(entry.getCausedBy(), newList);

                //更新
                int index = errors.indexOf(sameCause);
                errors.set(index, yieldEntry);

            }
        }

        return this;
    }

    @Override
    public String toString() {
        return "OutputImpl{" +
                "errors=" + errors +
                ", success=" + success +
                '}';
    }
}
