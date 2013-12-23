package com.hand.push.core.domain;

import com.hand.push.dto.PushEntry;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created with IntelliJ IDEA.
 * User: emerson
 * Date: 12/16/13
 * Time: 2:09 PM
 */
public class NodeResult {

    private final ConcurrentMap<Throwable,List<ErrorRequestEntry>> categoriedErrorEntries  = new ConcurrentHashMap<Throwable, List<ErrorRequestEntry>>();


    private final List<ErrorRequestEntry> errorEntries;

    public NodeResult() {
        errorEntries = new LinkedList<ErrorRequestEntry>();
    }

    public static NodeResult empty() {
        return new NodeResult();
    }

    public static NodeResult success() {
        return new NodeResult();
    }

    public static NodeResult error(Throwable message, PushEntry source) {
        return new NodeResult().addError(message, Arrays.asList(source));

    }

    public static NodeResult error(Throwable message, List<PushEntry> source) {
        return new NodeResult().addError(message,source) ;

    }


    public NodeResult addError(Throwable causedBy, List<PushEntry> source) {
        return this.yieldErrorEntries(causedBy,source);
    }

    public boolean hasError() {
        return errorEntries.size() != 0;
    }

    public List<ErrorRequestEntry> getErrorList() {
        return Collections.unmodifiableList(errorEntries);
    }

    private NodeResult yieldErrorEntries(Throwable causedBy, List<PushEntry> entries) {

        if (entries != null && (!entries.isEmpty()) && causedBy!= null){
            //1.根据cause类型，查看当前存储的错误信息中有没有相同原因的
            ErrorRequestEntry sameCause = null;
            for (ErrorRequestEntry stored : errorEntries) {
                if (stored.getCausedBy().getClass().equals(causedBy.getClass())) {
                    sameCause = stored;
                    break;
                }
            }

            //说明没有相同原因的
            if (sameCause == null) {
                errorEntries.add(new ErrorRequestEntry(causedBy,entries));
            } else {
                //存在相同，合并
                List<PushEntry> newList = new LinkedList<PushEntry>(sameCause.getData());
                newList.addAll(entries);
                ErrorRequestEntry yieldEntry = new ErrorRequestEntry(causedBy, newList);

                errorEntries.remove(sameCause);
                errorEntries.add(yieldEntry);
            }
        }



        return this;
    }

    @Override
    public String toString() {
        return "NodeResult{" +
                "errorEntries=" + errorEntries +
                '}';
    }
}
