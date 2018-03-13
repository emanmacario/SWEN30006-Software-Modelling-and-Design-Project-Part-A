package strategies;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import automail.Clock;

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
	
	
	// Hold all of our mail in a simple mail pool, sorted
	// on destination floor
	private ArrayList<MailItem> itemPool;
	
	
	// Class constructor
	public MyMailPool() {
		itemPool = new ArrayList<MailItem>();
	}
	
	

	/**
     * Adds an item to the mail pool
     * @param mailItem the mail item being added.
     */
	@Override
	public void addToPool(MailItem mailItem) {
		itemPool.add(mailItem);
		
	}
	
	
	/**
     * @param tube refers to the pack the robot uses to deliver mail.
     * @param strong is whether the tube belongs to a strong robot.
     */
	@Override
	public void fillStorageTube(StorageTube tube, boolean strong) {
		
		// First we sort the mail pool
		Collections.sort(itemPool, comparatorFinal);
		
		this.printMailPool();
		
		// Maximum capacity of the tube
		int maximumCapacity = tube.MAXIMUM_CAPACITY;
		
		
		// The max weight limit for the current robot
		int weightLimit = strong ? Integer.MAX_VALUE : 2000;
		
		// The number of additional items we can put in the tube
		int toAdd = maximumCapacity - tube.getSize();
		
		
		// Try to add the highest possible priority
		// item for each empty space in the storage tube
		for (int i = 0; i < toAdd; i++) {
			MailItem itemToAdd = getHighestPriorityMail(weightLimit);
			
			if (itemToAdd != null) {
				try {
					tube.addItem(itemToAdd);
					itemPool.remove(itemToAdd);
				} catch (TubeFullException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	
	// Returns if an acceptable weight mail item
	// is ok to be delivered
	private MailItem getHighestPriorityMail(int weightLimit) {
		
		MailItem mailItem = null;
		for (int i = itemPool.size()-1; i >= 0; i--) {
			if (itemPool.get(i).getWeight() < weightLimit) {
				mailItem = itemPool.get(i);
				break;
			}
		}
		
		return mailItem;
	}
	
	
	// Print the entire (sorted mailpool)
	private void printMailPool() {
		
		System.out.println("\n================= MailPool Contents ====================");
		
		for (MailItem mi : itemPool) {
			System.out.println(mi);
		}
		System.out.println("=========================================================\n");
		
	}
	
	
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
		
		// The time estimate between mail arriving,
		// and mail reaching its delivery destination
		int estimatedTime = Clock.Time()-mailItem.getArrivalTime()+mailItem.getDestFloor();
				
		return Math.pow(estimatedTime, 1.1)*(1.0+Math.sqrt(priority));
	}
}