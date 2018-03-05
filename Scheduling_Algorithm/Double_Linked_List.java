package assignment02_scheduling;

import java.util.ArrayList;

public class Double_Linked_List<PCB_Structure> {

    Node head;
    Node tail;
    int size;
    Node iterator;
    Node iterator2;

    public Double_Linked_List() {
        size = 0;
    }

    public class Node {

        PCB_Structure element;
        Node next;
        Node prev;

        public Node(PCB_Structure element, Node next, Node prev) {
            this.element = element;    	
            this.next = next;
            this.prev = prev;
        }
    }

    public void clear_iterator2() {
        iterator2 = head;
    }

    synchronized public void clear_iterator() {
        iterator = head;
    }

    public int size() {
        return size;
    }

    synchronized public boolean isEmpty() {
        return size == 0;
    }
    
    synchronized int getListElement(int index){
    	return 0;
    }
    
    synchronized public void add(PCB_Structure element) {
        Node tmp = new Node(element, null, tail);
        if (tail != null) {
            tail.next = tmp;
        }
        tail = tmp;
        if (head == null) {
            head = tmp;
        }
        size++;
        //tail.next = head; //Making the double linked list circular
        //System.out.println("ELEMENT ADDED : " + tail.element);
    }
    
    synchronized public void removeAtHead() {
        head = head.next;
        //size--;
        //tail.next = head; //Making the double linked list circular
        //System.out.println("ELEMENT ADDED : " + tail.element);
    }
    
    synchronized public void remove_index(int index){
        Node iterator_elem;
    	if(index==0){
        	head = head.next;
        	//head.prev = null;
        }
        else{
        	//clear_iterator();
        	iterator_elem = head;
        	for(int i=0;i<index;i++){
        		iterator_elem = iterator_elem.next;
        	}
        	if(iterator_elem.next == null){
        		iterator_elem.prev.next = null;
        		//iterator_elem.prev = null;
        	}
        	else{
        		iterator_elem.next.prev = iterator_elem.prev;
        		iterator_elem.prev.next = iterator_elem.next;
        	}	
        //printall();
        	//clear_iterator();
        }
        //size--;
    }
    
    
    
    public void printall() {
        for (Node i = this.head; i != null; i = i.next) {
            System.out.println(i.element);
        }
    }

}
