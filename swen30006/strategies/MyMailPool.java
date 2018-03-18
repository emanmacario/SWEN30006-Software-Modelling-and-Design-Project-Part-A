/**
 * SWEN30006 Software Modelling and Design
 * Semester 1, 2017
 * Project (Part A)
 * 
 * Name: Emmanuel Macario
 * Student Number: 831659
 * 
 */


/** Package Name */
package strategies;


/** Useful classes from the Java standard library */
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import automail.Clock;


/** Importing classes from the implementation */
import automail.MailItem;
import automail.StorageTube;
import automail.PriorityMailItem;
import exceptions.TubeFullException;



/**
 * My implementation of the MailPool.
 *
 */
public class MyMailPool implements IMailPool {
	
	/**
	 * The current pool of mail to be delivered
	 */
	private ArrayList<MailItem> itemPool;
	
	
	/** 
	 * Class constructor.
	 */
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
		
		// Sort the item pool
		Collections.sort(itemPool, floorComparator);
		
		
		// Print the sorted mail pool
		this.printMailPool();
		
		// The max weight limit for the current robot
		int weightLimit = strong ? Integer.MAX_VALUE : 2000;
		
		//  The maximum number of additional items we can put in the tube
		int toAdd = tube.MAXIMUM_CAPACITY - tube.getSize();
		
		
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
		/*
		ArrayList<MailItem> itemsToAdd = getBestMail(weightLimit, toAdd);
		
		for (int i = itemsToAdd.size()-1; i >= 0; i--) {
			try {
				tube.addItem(itemsToAdd.get(i));
			} catch (TubeFullException e) {
				e.printStackTrace();
			}
		}*/
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
	
	
	// Gets the best items by measure
	public ArrayList<MailItem> getBestMail(int weightLimit, int toAdd) {
		ArrayList<MailItem> itemsToAdd = new ArrayList<>();
		
		Collections.sort(itemPool, measureComparator);
		
		for (int i = 0; i < toAdd; i++) {
			
			MailItem itemToAdd = null;
			
			for (MailItem mailItem : itemPool) {
				if (mailItem.getWeight() < weightLimit) {
					itemToAdd = mailItem;
					break;
				}
			}
			
			if (itemToAdd != null) {
				itemsToAdd.add(itemToAdd);
				itemPool.remove(itemToAdd);
			}
		}
		
		Collections.sort(itemsToAdd, destinationComparator);
		
		return itemsToAdd;
		
	}
	
	
	// Print the entire (sorted mailpool)
	private void printMailPool() {
		
		System.out.println("\n================= MailPool Contents ====================");
		
		// Print the current time
		System.out.println("T: " + Clock.Time());
		
		for (MailItem mi : itemPool) {
			System.out.println(mi);
		}
		System.out.println("========================================================\n");
		
	}
	
	
	// Calculate the 'time taken' measure. Key
	// assumption is that time taken to deliver
	// is equal to the destination floor.
	private double calculateMeasure(MailItem mailItem, int priority) {
		
		// The time estimate between mail arriving,
		// and mail reaching its delivery destination
		int estimatedTime = Clock.Time()-mailItem.getArrivalTime()+mailItem.getDestFloor()+1;
				
		return Math.pow(estimatedTime, 1.1)*(1.0+Math.sqrt(priority));
	}
	
	
	
	// Compares mail items based on destination floor.
	// Within each destination floor echelon, sort
	// mail items according to their priority.
	private Comparator<MailItem> floorComparator = 
			new Comparator<MailItem>() {
		@Override
		public int compare(MailItem m1, MailItem m2) {
			
			
			// Compare firstly by destination floor
			if (m1.getDestFloor() != m2.getDestFloor()) {
				return m1.getDestFloor() - m2.getDestFloor();
			}

			
			// Then compare by priority
			int p1 = 0, p2 = 0;
			if (m1 instanceof PriorityMailItem) {
				p1 = ((PriorityMailItem)m1).getPriorityLevel();
			}
			if (m2 instanceof PriorityMailItem) {
				p2 = ((PriorityMailItem)m2).getPriorityLevel();
			}
			if (p1 != p2) {
				return (p1 - p2);
			}
			
			
			// Compare by arrival time, want mail that arrived first
			// to have greater importance.
			if (m1.getArrivalTime() != m2.getArrivalTime()) {
				return m2.getArrivalTime()-m1.getArrivalTime();
			}
			
			
			// Lastly compare by weight
			return m1.getWeight() - m2.getWeight();
		}
	};
	
	
	
	private Comparator<MailItem> destinationComparator =
			new Comparator<MailItem>() {
		@Override
		public int compare(MailItem m1, MailItem m2) {
			return m1.getDestFloor()-m2.getDestFloor();
		}
	};
	
	
	
	private Comparator<MailItem> measureComparator = 
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
			
			return Double.compare(calculateMeasure(m2,p2), calculateMeasure(m1,p1));
		}
	};
}