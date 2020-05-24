package org.anonymous.data;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class ParseContext {
    public String jsonStr;
    public Queue<Character> queue;
    public int size;
    public String currentStr;

    public ParseContext(){
        initStack();
    }

    public void initStack(){
        this.queue =new LinkedList<Character>();
        size=0;
    }

    public void pushChar(Character character){
        queue.add(character);
        size++;
    }

    public String popString(int strSize){
        StringBuilder sb=new StringBuilder();
        while(strSize>0&&size>0){
            sb.append(queue.poll());
            size--;
            strSize--;
        }
        return sb.toString();
    }

    public void clearN(int number){
        while(queue.size()>0&&number>0){
            queue.poll();
            number--;
        }
    }

    public void clearStack(){
        while(queue.size()!=0){
            queue.poll();
        }
    }
    public void clearCurrentStr(){
        this.currentStr=null;
    }

}
