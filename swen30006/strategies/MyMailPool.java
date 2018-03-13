package strategies;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


import automail.MailItem;
import automail.StorageTube;
import exceptions.TubeFullException;
import automail.PriorityMailItem;


public class MyMailPool implements IMailPool {
	
	// Idea: use a priority queue.
	// The comparator needs to prioritise based on 3 factors: floor, weight, priority
	
	/**
	 * For strong robots:
	 * - Want to get highest priority, highest weight, highest floor first.
	 * 
	 * 
	 * For weak robots:
	 * - Want to get highest priority, lowest weight, highest floor first.
	 */
	
	
	// Separate the priority mail from non-priority mail
	private ArrayList<PriorityMailItem> priorityPool;
	private ArrayList<MailItem> nonPriorityPool;
	
	
	
	
	// Class constructor
	public MyMailPool() {
		priorityPool = new ArrayList<PriorityMailItem>();
		nonPriorityPool = new ArrayList<MailItem>();
	}
	
	

	/**
     * Adds an item to the mail pool
     * @param mailItem the mail item being added.
     */
	@Override
	public void addToPool(MailItem mailItem) {
		
		if (mailItem instanceof PriorityMailItem) {
			priorityPool.add((PriorityMailItem)mailItem);
			Collections.sort(priorityPool, comparatorFinal);
		} else {
			nonPriorityPool.add(mailItem);
			Collections.sort(nonPriorityPool, comparatorFinal);
		}
	}
	
	
	/**
     * @param tube refers to the pack the robot uses to deliver mail.
     * @param strong is whether the tube belongs to a strong robot.
     */
	@Override
	public void fillStorageTube(StorageTube tube, boolean strong) {
		
		// Check that tube isn't already full
		if (tube.isFull()) {
			return;
		}
		
		while (!tube.isFull()) {
			
			if (priorityPool.size() > 0) {
				try {
					tube.addItem(priorityPool.get(priorityPool.size()-1));
				} catch (TubeFullException e) {
					e.printStackTrace();
				}
			} else if (nonPriorityPool.size() > 0) {
				try {
					tube.addItem(nonPriorityPool.get(nonPriorityPool.size()-1));
				} catch (TubeFullException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	// Places highest priority on highest floor, then earliest
	// arrival time of MailItem
	private Comparator<MailItem> veryNormal =
			new Comparator<MailItem>() {
		@Override
		public int compare(MailItem m1, MailItem m2) {
			
			// Next compare based on floor
			if (m1.getDestFloor() != m2.getDestFloor()) {
				
				return m1.getDestFloor()-m2.getDestFloor();
				
			} 
			// Lastly compare on arrival time. Want items that arrived earlier
			// to have higher priority.
			else if (m1.getArrivalTime() != m2.getArrivalTime()) {
				
				return m2.getArrivalTime()-m1.getArrivalTime();
			}
			
			return 0;
		}
	};
	
	
	
	
	
	
	private int getNonPriorityPoolSize(int weightLimit) {
		
		int count = 0;
		for (MailItem m : nonPriorityPool) {
			if (m.getWeight() <= weightLimit) {
				count++;
			}
		}
		return count;
	}
	
	
	
	
	private int getPriorityPoolSize(int weightLimit){
		
		int count = 0;
		for (MailItem m : priorityPool) {
			if (m.getWeight() <= weightLimit) {
				count++;
			}
		}
		return count;
	}
	
	

	
	
	private Comparator<MailItem> heavy = 
			new Comparator<MailItem>() {
		@Override
		public int compare(MailItem m1, MailItem m2) {
			
			// Compare based on weight foremost
			if (m1.getWeight() != m2.getWeight()) {
				
				return m1.getWeight()-m2.getWeight();
				
			} else {
				
				return veryNormal.compare(m1, m2);
			}
		}
	};
	
	
	
	private Comparator<MailItem> light =
			new Comparator<MailItem>() {
		@Override
		public int compare(MailItem m1, MailItem m2) {
			
			if (m1.getWeight() != m2.getWeight()) {
				return m2.getWeight()-m1.getWeight();
			} else {
				return veryNormal.compare(m1,  m2);
			}
		}
	};
	
	
	// Places greatest priority on highest priority, and heaviest.
	private Comparator<PriorityMailItem> priorityHeavy = 
			new Comparator<PriorityMailItem>() {
		@Override
		public int compare(PriorityMailItem p1, PriorityMailItem p2) {
			
			if (p1.getPriorityLevel() != p2.getPriorityLevel()) {
				return p1.getPriorityLevel()-p2.getPriorityLevel();
			}
			return heavy.compare(p1, p2);
		}
	};
	
	
	
	// Places greatest priority on highest priority, and lightest.
	private Comparator<PriorityMailItem> priorityLight = 
			new Comparator<PriorityMailItem>() {
		@Override
		public int compare(PriorityMailItem p1, PriorityMailItem p2) {
			
			if (p1.getPriorityLevel() != p2.getPriorityLevel()) {
				return p1.getPriorityLevel()-p2.getPriorityLevel();
			}
			return light.compare(p2, p1);
		}
	};
	
	
	// Comparator based on the given 'time taken'
	// measure provided in the specification
	private Comparator<MailItem> comparatorFinal = 
			new Comparator<MailItem>() {
		@Override
		public int compare(MailItem m1, MailItem m2) {
			
			int p1 = 0, p2 = 0;
			
			if (m1 instanceof PriorityMailItem) {
				p1 = ((PriorityMailItem)m1).getPriorityLevel();
			}
			
			if (m2 instanceof PriorityMailItem) {
				p2 = ((PriorityMailItem)m2).getPriorityLevel();
			}
			
			return Double.compare(calculateMeasure(m1, p1), 
					calculateMeasure(m2, p2));
		}
	};
	
	
	// Calculate the 'time taken' measure. Key
	// assumption is that time taken to deliver
	// is equal to the destination floor.
	private double calculateMeasure(MailItem mailItem, int priority) {
		return Math.pow(mailItem.getDestFloor(), 1.1)
				*(1.0+Math.sqrt(priority));
	}
}



















