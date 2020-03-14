import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.EmptyBorder;

import maryb.player.Player;
import maryb.player.PlayerEventListener;

public class GBMP3Player extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static JPanel contentPane;
	public static String directoryChoosed;
	private java.awt.List songsList;
	maryb.player.Player p;
	List<File> songsFileList;

	private Timer myTimer;
	private JButton btnPrevious;
	private JButton btnPlay;
	private JButton btnNext;
	private JButton browseBtn;
	private JProgressBar pgBar;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GBMP3Player frame = new GBMP3Player();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public GBMP3Player() {
		setTitle("GB-AMP");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 634, 488);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		contentPane.add(getBtnBrowse());
		contentPane.add(getSongsList());
		contentPane.add(getBtnPrevious());
		contentPane.add(getBtnPlay());
		contentPane.add(getBtnNext());
		contentPane.add(getPgBar());

		songsFileList = new ArrayList<>();

	}

	private java.awt.List getSongsList() {
		if (songsList == null) {
			songsList = new java.awt.List();
			songsList.setBounds(10, 10, 598, 326);
			songsList.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {

					btnNext.setVisible(true);
					btnPrevious.setVisible(true);
					pgBar.setVisible(true);
					play(songsList.getSelectedIndex());

				}
			});

			songsList.addKeyListener(new KeyListener() {

				@Override
				public void keyTyped(KeyEvent e) {

				}

				@Override
				public void keyReleased(KeyEvent e) {

					long currentPos = getPlayer().getCurrentPosition();
					long maxPos = getPlayer().getTotalPlayTimeMcsec();
					long newPos;

					if (e.getKeyCode() == KeyEvent.VK_F9) {
						newPos = currentPos - 10000000;
						if (newPos > 0)
							getPlayer().seek(newPos);
						else
							btnPrevious.doClick();

					} else if (e.getKeyCode() == KeyEvent.VK_F11) {
						newPos = currentPos + 10000000;
						if (newPos < maxPos)
							getPlayer().seek(newPos);
						else
							btnNext.doClick();

					} else if (e.getKeyCode() == KeyEvent.VK_ENTER) {

						getPlayer().seek(0);

					} else if (e.getKeyCode() == KeyEvent.VK_F10) {
						playPauseManager();
					}

				}

				@Override
				public void keyPressed(KeyEvent e) {

				}
			});

			songsList.setVisible(false);
		}
		return songsList;
	}

	private JButton getBtnBrowse() {
		if (browseBtn == null) {
			browseBtn = new JButton("Browse Music Folder");
			browseBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					songsList.setVisible(false);
					JFileChooser songsDirectoryChooser = new JFileChooser(
							"Select the directory. We will look there for songs.");
					songsDirectoryChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					songsDirectoryChooser.setCurrentDirectory(new File("D:\\Downloads\\Downloaded Songs"));
					songsDirectoryChooser.showDialog(contentPane, "Open");
					try {
						directoryChoosed = songsDirectoryChooser.getSelectedFile().getAbsolutePath();
						songsFileList = AudioFileDetector.searchAudio(directoryChoosed);
						songsList.removeAll();

						// populating the tables with the names of the songs.
						for (File file : songsFileList) {
							songsList.add(file.getName());
						}

						if (songsFileList.isEmpty()) {
							songsList.add(
									"No any songs found in this directory. Please choose another directory to search song for.");
						}
						songsList.setVisible(true);

					} catch (NullPointerException e2) {
						e2.printStackTrace();
					}
				}

			});
			browseBtn.setBounds(10, 342, 598, 23);
		}
		return browseBtn;
	}

	private JButton getBtnPrevious() {
		if (btnPrevious == null) {
			btnPrevious = new JButton("Previous");
			btnPrevious.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					songsList.select(songsList.getSelectedIndex() - 1);
					play(songsList.getSelectedIndex());
				}
			});
			btnPrevious.setBounds(10, 376, 89, 23);
			btnPrevious.setVisible(false);
		}
		return btnPrevious;
	}

	private JButton getBtnPlay() {
		if (btnPlay == null) {
			btnPlay = new JButton("Play");
			btnPlay.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					playPauseManager();
					if (songsList.isVisible() == false) {
						browseBtn.doClick();
					}
				}
			});
			btnPlay.setBounds(257, 376, 89, 23);
		}
		return btnPlay;
	}

	private JButton getBtnNext() {
		if (btnNext == null) {
			btnNext = new JButton("Next");
			btnNext.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					songsList.select(songsList.getSelectedIndex() + 1);
					play(songsList.getSelectedIndex());
				}
			});
			btnNext.setBounds(519, 376, 89, 23);
			btnNext.setVisible(false);
		}
		return btnNext;
	}

	private Player getPlayer() {
		if (p == null) {
			p = new Player();
			p.setListener(new PlayerEventListener() {

				@Override
				public void stateChanged() {
					// TODO Auto-generated method stub

				}

				@Override
				public void endOfMedia() {
					btnNext.doClick();

				}

				@Override
				public void buffer() {
					// TODO Auto-generated method stub

				}
			});
		}
		return p;
	}

	private Timer getTimer() {
		if (myTimer == null) {
			myTimer = new Timer();
			myTimer.scheduleAtFixedRate(new TimerTask() {

				@Override
				public void run() {
					if (getPlayer().getTotalPlayTimeMcsec() > 0) {
						pgBar.setValue((int) (getPlayer().getCurrentPosition() / 1000000));
						pgBar.setMaximum((int) getPlayer().getTotalPlayTimeMcsec() / 1000000);
					}

					/*
					 * System.out.println(" Current Position of Media Player = " + (int)
					 * (getPlayer().getCurrentPosition() / 1000000)); System.out
					 * .println(" Total Play time in micro secs " +
					 * getPlayer().getTotalPlayTimeMcsec() / 1000000);
					 * System.out.println(" Progress Bar current position " + pgBar.getValue());
					 * System.out.println(" Progress Bar max position " + pgBar.getMaximum());
					 * System.out.println(getPlayer().getState());
					 */
				}
			}, 0, 100);
		}
		return myTimer;
	}

	private void play(int index) {
		try {
			getPlayer().pauseSync();
			getPlayer().stopSync();
			getPlayer().setSourceLocation((songsFileList.get(index).getAbsolutePath()));
			getPlayer().playSync();
			getTimer();
			btnPlay.setText("Pause");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private JProgressBar getPgBar() {
		if (pgBar == null) {
			pgBar = new JProgressBar();
			pgBar.setBounds(10, 410, 598, 28);
			pgBar.setVisible(false);
		}
		return pgBar;
	}

	private void playPauseManager() {
		if (btnPlay.getText().equals("Pause")) {
			btnPlay.setText("Play");
			// System.out.println(p.getState());
			getPlayer().pause();
		} else if (btnPlay.getText().equals("Play")) {
			if (songsFileList.size() > 0) {
				btnPlay.setText("Pause");
				if (songsList.getSelectedIndex() != -1) {
					// System.out.println(p.getState());
					getPlayer().play();
				} else {
					btnPrevious.setVisible(true);
					btnNext.setVisible(true);
					pgBar.setVisible(true);
					songsList.select(0);
					play(songsList.getSelectedIndex());
				}
			}
		}
	}
}
