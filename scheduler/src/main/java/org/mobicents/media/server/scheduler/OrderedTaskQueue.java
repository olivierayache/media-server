/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc. and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.mobicents.media.server.scheduler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

/**
 * Implements queue of tasks.
 * 
 * 
 * @author kulikov
 */
public class OrderedTaskQueue {
	private final Object LOCK = new Object();

    private final static int QUEUE_SIZE = 100;

    //inner holder for tasks
    private List<Task>[] taskList=new List[2];
    
    private Integer activeIndex=0;
    
    public OrderedTaskQueue() {
        //intitalize task list
    	taskList[0] = new ArrayList(QUEUE_SIZE);
    	taskList[1] = new ArrayList(QUEUE_SIZE);
    }

    public Collection<Task> getTasks() {
    	synchronized(LOCK) {
    		return taskList[activeIndex];
    	}
    }

    /**
     * Shows if this queue is empty.
     * 
     * @return true if queue is currently empty
     */
    public boolean isEmpty() {
    	synchronized(LOCK) {
    		return taskList[activeIndex].isEmpty();
    	}
    }
    
    /**
     * Queues specified task using tasks dead line time.
     * 
     * @param task the task to be queued.
     * @return TaskExecutor for the scheduled task.
     */
    public void accept(Task task) {
    	synchronized(LOCK) {
    		if(!taskList[(activeIndex+1)%2].contains(task))
    			taskList[(activeIndex+1)%2].add(task);
    	}
    }
    
    /**
     * Retrieves the task with earliest dead line and removes it from queue.
     * 
     * @return task which has earliest dead line
     */
    public Task poll() {
    	synchronized(LOCK) {
    		if(taskList[activeIndex].isEmpty())
    			return null;
    
    		return taskList[activeIndex].remove(0);
    	}
    }    
    
    public Task pollAny() {
    	synchronized(LOCK) {    	
    		if(taskList[activeIndex].isEmpty())
    		{
    			activeIndex=(activeIndex+1)%2;
    			if(taskList[activeIndex].isEmpty())
    				return null;
    		}
    	
    		return taskList[activeIndex].remove(0);
    	}
    }
	
    /**
     * Retrieves but do not remove earliest dead line task.
     * 
     * @return task.
     */
    public Task peek() {
    	synchronized(LOCK) {
    		return taskList[activeIndex].get(0);
    	}
    }

    public void changePool()
    {
    	synchronized(LOCK) {    	
    		activeIndex=(activeIndex+1)%2;
    	}
    }
    
    public Object getMonitor() {
        return LOCK;
    }
    
    /**
     * Clean the queue.
     */
    public void clear() {
    	synchronized(LOCK) {
    		taskList[0].clear();
    		taskList[1].clear();
    	}
    }
    
    /**
     * Gets the size of this queue.
     * 
     * @return the size of the queue.
     */
    public int size() {
    	synchronized(LOCK) {
    		return taskList[activeIndex].size();
    	}
    }

    protected void remove(Task task) {
    	synchronized(LOCK) {
    		taskList[activeIndex].remove(task);
    	}
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Queue[");
        
        int len = Math.min(30, taskList[activeIndex].size());
        for (int i = 0; i < len -1; i++) {
        	//sb.append(taskList[activeIndex].get(i).getPriority());
            sb.append(",");
        }
        
        //if(!taskList[activeIndex].isEmpty())
        //	sb.append(taskList[activeIndex].get(len - 1).getPriority());
        sb.append("]");
        return sb.toString();
    }
}