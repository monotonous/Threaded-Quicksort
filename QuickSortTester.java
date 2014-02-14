/*
 * QuickSorter.java
 * 
 * Author: Joshua Parker
 * 
 * Shows a graphical representation of quicksort 
 * in both sequential and parallel iterations
 */

import java.lang.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.util.*;
import javax.swing.*;

public class QuickSortTester<T,V> implements Runnable {
	public static int vect = 32, leftSleep = 100,
					  rightSleep = 100, SwapSleep = 500;
	public static void main(String[] args) {
		try {
			if(args.length == 4){
				vect = vectRange(args[0]);
				if(vect == -1)
					throw new Exception("Vector values must be between 0-100");
				leftSleep = swapRange(args[1]);
				rightSleep = swapRange(args[2]);
				SwapSleep = swapRange(args[3]);
				if(leftSleep == -1 || rightSleep == -1 || SwapSleep== -1)
					throw new Exception("Sleep times must be between 0-7000");
			}else if(args.length != 0){
				throw new Exception("Must have 4 parameters or 0 for default values");
			}
		    SwingUtilities.invokeAndWait(new QuickSortTester());
		    System.out.println("*** GUI started");
		} catch (Exception ex) {
		    System.err.println("*** "+ex.getMessage());
		}
	}

	private static int vectRange(String check){
		int in = Integer.parseInt(check);
		if(in >= 0 && in <= 100) return in;
		return -1;
	}

	private static int swapRange(String check){
		int in = Integer.parseInt(check);
		if(in >= 0 && in <= 7000) return in;
		return -1;
	}

	JButton BoredButton;
	JButton WorkerButton;
	JButton WorkerButton2;
	SorterPanel MySortPanel;
	JLabel StatusBar;
	
	public void run() {
		JFrame frame = new JFrame();
        frame.setTitle("My QuickSort Demo");
        Font font = new Font("Monospaced", Font.BOLD, 18);

        BoredButton = new JButton("I am bored");
        BoredButton.setFont(font);
        BoredButton.setPreferredSize(new Dimension(200, 30));
        BoredButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                BoredButton_Click();
            }
        });

        WorkerButton = new JButton("QuickSort Sequential");
        WorkerButton.setFont(font);
        WorkerButton.setPreferredSize(new Dimension(270, 30));
        WorkerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                WorkerButton_Click(vect);
            }
        });

        WorkerButton2 = new JButton("QuickSort Parallel");
	
        WorkerButton2.setFont(font);
        WorkerButton2.setPreferredSize(new Dimension(270, 30));
        WorkerButton2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                WorkerButton2_Click();
            }
        });

        JPanel strip = new JPanel(new FlowLayout(FlowLayout.CENTER));
        strip.add(BoredButton);
        strip.add(WorkerButton);
        strip.add(WorkerButton2);
        frame.getContentPane().add(strip, BorderLayout.NORTH);

		StatusBar = new JLabel();
		StatusBar.setFont(font);
		StatusBar.setPreferredSize(new Dimension(800, 20));
        frame.getContentPane().add(StatusBar, BorderLayout.SOUTH);

        MySortPanel = new SorterPanel();
        frame.getContentPane().add(MySortPanel, BorderLayout.CENTER);

        frame.getRootPane().setDefaultButton(BoredButton);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 400);
        frame.setResizable(true);
        frame.setVisible(true);
	}

    public void BoredButton_Click() {
		String text = Calendar.getInstance().getTime().toString();
        StatusBar.setText(text);
    }

    void _WorkerButton_Click(int vect, int depth) {
		int[] values = new int[vect];
		for (int i = 0; i < values.length; i++) {
		    values[i] = (int)Math.round(Math.random() * (MySortPanel.getHeight()-10));
		}
		MySortPanel.setValues(values, depth);
		Thread sorterThread = new Thread(MySortPanel);
		sorterThread.start();
	}

    public void WorkerButton_Click(int vect) {
		WorkerButton.setEnabled(false);
		WorkerButton2.setEnabled(false);
		_WorkerButton_Click(vect, 0);
	}

	public void WorkerButton2_Click() {
		WorkerButton.setEnabled(false);
		WorkerButton2.setEnabled(false);
		
		Runtime runtime = Runtime.getRuntime();
        int noOfProcessors = runtime.availableProcessors();

        _WorkerButton_Click(vect, (noOfProcessors * 2));
    }

	class SorterPanel extends JComponent implements Runnable {
		int[] values;
		int depth;
		
		int width;
		Graphics2D g2;
		Color back;

		// gui section
		public void setValues(int[] values , int depth) {
			this.values = values;
			this.depth = depth;
			print(values);
			width = super.getWidth() / values.length;
			repaint();
		}

		public void print(int[] values) {
		    System.out.println("... values");
		    for (int c : values)
		    	System.out.println(c);
        }

		@Override
		public void paintComponent(Graphics g) {
			if (values == null) return;

			g2 = (Graphics2D) g;
			back = g2.getBackground();

			for (int i = 0; i < values.length; i++) {
			    g2.draw(new Rectangle2D.Double(width*i+1, 0, width-2, values[i]));
			}
		}

		void _redraw(int i) {
			g2 = (Graphics2D) super.getGraphics();
			g2.draw(new Rectangle2D.Double(width*i+1, 0, width-2, values[i]));
		}

		void _mark(int i, Color m) {
			g2 = (Graphics2D) super.getGraphics();
			Color pen = g2.getColor();
			g2.setColor(m);
			g2.fill(new Rectangle2D.Double(width*i+2, 1, width-4, values[i]-2));
			g2.setColor(pen);
		}

		void _erase(int i) {
			g2 = (Graphics2D) super.getGraphics();
			g2.clearRect(width*i+1, 0, width-1, values[i]+1);
		}

		// background section
		void mark(final int i, final Color m) {
			try {
				SwingUtilities.invokeAndWait(new Runnable() {
					public void run() {
						_mark(i, m);
					}
				});

			} catch (Exception ex) {
				System.out.println(ex);
			}
		}

		void erase(final int i) {
			try {
				SwingUtilities.invokeAndWait(new Runnable() {
					public void run() {
						_erase(i);
					}
				});

			} catch (Exception ex) {
				System.out.println(ex);
			}
		}

	    void redraw(final int i) {
			try {
				SwingUtilities.invokeAndWait(new Runnable() {
					public void run() {
		                _redraw(i);
		                _mark(i, back);
					}
				});

			} catch (Exception ex) {
				System.out.println(ex);
			}
		}

		void done(final int i) {
			mark(i, Color.GRAY);
		}

		void pivoton(final int i) {
			mark(i, Color.BLACK);
		}

		void lefton(final int i) {
			mark(i, Color.GREEN);
		}

		void righton(final int i) {
			mark(i, Color.RED);
		}

		void off(final int i) {
			mark(i, back);
		}

		public void run() {
			QuickSort(values, 0, values.length - 1, this , depth);
			done();
			print(values);
			System.out.println("*** DONE");
		}
		public void done(){
	        WorkerButton.setEnabled(true);
	        WorkerButton2.setEnabled(true);
	    }
	}

	void QuickSort(final int array[], final int left, final int right, final SorterPanel sortpan, final int depth) {
        if (left >= right) return;
        final int pivot = Partition(array, left, right, sortpan);
        if (pivot < 0) return;
        if(depth > 0){        	
			new SwingWorker<T, int[]>() {
				@Override
				protected T doInBackground() throws Exception {
					QuickSort(array, left, pivot-1, sortpan, depth - 1);
					QuickSort(array, pivot, right, sortpan, depth - 1);
					publish(process(array));
					return null;
				}
		        
		        protected int[] process(int[] array){
		        	System.out.println("... values");
		        	for(int i = 0; i < array.length; i++)
		        		System.out.println(array[i]);
		        	return array;
		        }
		        public void done(){
			        WorkerButton.setEnabled(true);
			        WorkerButton2.setEnabled(true);
			    }
			}.execute();
        }else{
            QuickSort(array, left, pivot-1, sortpan, depth);
            QuickSort(array, pivot, right, sortpan, depth);
        }
    }

	int Partition(final int array[], final int left, final int right, final SorterPanel sortpan) {
        final int LEFT = QuickSortTester.leftSleep;
		final int RIGHT = QuickSortTester.rightSleep;
		final int SWAP = QuickSortTester.SwapSleep;
		final int STEP = 100;

		int debug = 0;

        int leftIdx = left;
        int rightIdx = right;
        final int pivotIdx = (left + right) / 2;
        final int pivot = array[pivotIdx];

        if (leftIdx <= right) sortpan.lefton(leftIdx);
        if (left <= rightIdx) sortpan.righton(rightIdx);

        while (leftIdx <= rightIdx) {
            while (true) {
			    try { Thread.sleep(LEFT); } catch (Exception ex) { System.out.println(ex); }

			    if (array[leftIdx] >= pivot) break;

                if (leftIdx <= right) sortpan.off(leftIdx);
                leftIdx += 1;
                if (leftIdx <= right) sortpan.lefton(leftIdx);
            }

            while (true) {
                try { Thread.sleep(RIGHT); } catch (Exception ex) { System.out.println(ex); }

                if (pivot >= array[rightIdx]) break;

                if (left <= rightIdx) sortpan.off(rightIdx);
                rightIdx -= 1;
                if (left <= rightIdx) sortpan.righton(rightIdx);
            }

            if (leftIdx <= rightIdx) {
                try { Thread.sleep(SWAP); } catch (Exception ex) { System.out.println(ex); }

                if (leftIdx <= right) sortpan.erase(leftIdx);
                if (left <= rightIdx) sortpan.erase(rightIdx);

                int temp = array[rightIdx];
                array[rightIdx] = array[leftIdx];
                array[leftIdx] = temp;

                if (leftIdx <= right) sortpan.redraw(leftIdx);
                if (left <= rightIdx) sortpan.redraw(rightIdx);

                leftIdx += 1;
                if (leftIdx <= right) sortpan.lefton(leftIdx);
                rightIdx -= 1;
                if (left <= rightIdx) sortpan.righton(rightIdx);

                try { Thread.sleep(STEP); } catch (Exception ex) { System.out.println(ex); }
                if (++debug > 100) return -1;
            }
        }

        if (leftIdx <= right) sortpan.off(leftIdx);
        if (left <= rightIdx) sortpan.off(rightIdx);

        return leftIdx;
    }
}