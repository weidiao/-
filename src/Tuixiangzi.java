import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.Stack;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class Tuixiangzi extends JFrame implements ActionListener, ItemListener {
	public static void main(String[] args) {
		new Tuixiangzi();
	}

	class ChooseLevelDialog extends JDialog {
		public ChooseLevelDialog() {
			super(Tuixiangzi.this);
			setTitle("选关");
			setModal(true);
			setSize(80, 200);
			setLocationRelativeTo(this);
			Integer a[] = new Integer[50];
			for (int i = 0; i < a.length; i++)
				a[i] = i + 1;
			final JList<Integer> list = new JList<>(a);
			add(new JScrollPane(list));
			list.setSelectedIndex(panel.level - 1);
			list.addListSelectionListener(new ListSelectionListener() {

				@Override
				public void valueChanged(ListSelectionEvent e) {
					panel.level = list.getSelectedIndex() + 1;
					panel.loadLevel();
					chooseLevelDialog.setVisible(false);
				}
			});
		}
	}

	MainPanel panel = new MainPanel();
	ChooseLevelDialog chooseLevelDialog = new ChooseLevelDialog();
	Sound sound = new Sound();

	JLabel lb;
	JLabel lb2;

	JMenu optionsMenu = new JMenu("选项");
	String[] options = new String[] { "重来", "悔一步", "第一关", "上一关", "下一关", "最终关", "选关" };
	JMenu musicMenu = new JMenu("音乐");
	String[] musics = new String[] { "默认", "琴箫合奏", "泡泡堂", "灌篮高手", "eyes on me" };
	JMenu helpMenu = new JMenu("帮助");
	JMenuItem about = new JMenuItem("关于推箱子");
	JCheckBoxMenuItem musicCheckecBoxMenuItem = new JCheckBoxMenuItem("音乐", true);
	JCheckBox musicCheckBox = new JCheckBox("音乐");
	JComboBox<String> musicComboBox = new JComboBox<>(musics);
	boolean musicEnabled = true;

	Tuixiangzi() {
		super("推箱子");
		setSize(720, 720);
		setResizable(false);
		setLocationRelativeTo(null);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				panel.save();
				System.exit(0);
			}
		});

		setLayout(null);
		getContentPane().setBackground(Color.black);

		for (String s : options) {
			JMenuItem item = new JMenuItem(s);
			optionsMenu.add(item);
			item.addActionListener(this);
		}
		optionsMenu.add(musicCheckecBoxMenuItem);
		musicCheckecBoxMenuItem.addActionListener(listenMusicState);
		ButtonGroup g = new ButtonGroup();
		for (String s : musics) {
			JRadioButtonMenuItem item = new JRadioButtonMenuItem(s);
			item.addItemListener(this);
			musicMenu.add(item);
			g.add(item);
		}
		helpMenu.add(about);
		about.addActionListener(this);

		JMenuBar bar = new JMenuBar();
		bar.add(optionsMenu);
		bar.add(musicMenu);
		bar.add(helpMenu);
		setJMenuBar(bar);

		lb = new JLabel("JAVA推箱子v2.0版", SwingConstants.CENTER);
		lb2 = new JLabel("更换音乐", SwingConstants.CENTER);
		lb.setBounds(100, 20, 400, 20);
		lb.setForeground(Color.white);
		lb2.setBounds(625, 500, 55, 20);
		lb2.setForeground(Color.white);

		for (int i = 0; i < options.length; i++) {
			JButton button = new JButton(options[i]);
			add(button);
			button.addActionListener(this);
			button.setBounds(625, 100 + 50 * i, 80, 30);
		}
		musicCheckBox.setBounds(625, 100 + 50 * options.length, 80, 30);
		musicCheckBox.addActionListener(listenMusicState);
		musicComboBox = new JComboBox<>(musics);
		musicComboBox.addItemListener(this);
		musicComboBox.setBounds(625, 100 + 50 * options.length + 100, 80, 30);
		musicComboBox.setSelectedItem(0);
		add(lb);
		add(lb2);
		add(musicCheckBox);
		add(musicComboBox);
		add(panel);
		panel.loadLevel();

		setMusicState(musicEnabled);
		setVisible(true);
		panel.requestFocus();
	}

	public void actionPerformed(ActionEvent e) {
		panel.requestFocus();
		if (e.getActionCommand().equals("重来")) {
			panel.loadLevel();
		} else if (e.getActionCommand().equals("上一关")) {
			if (panel.level == 1)
				return;
			panel.level--;
			panel.loadLevel();
		} else if (e.getActionCommand().equals("下一关")) {
			if (panel.level == MainPanel.MAX_LEVEL)
				return;
			panel.level++;
			panel.loadLevel();
		} else if (e.getSource() == about) {
			JOptionPane.showMessageDialog(this, "JAVA推箱子v2.0版\n开发者：施超\nEmail:   shichaoling1@126.com\nQQ:   450400704");
		} else if (e.getActionCommand().equals("选关")) {
			chooseLevelDialog.setVisible(true);
		} else if (e.getActionCommand().equals("第一关")) {
			panel.level = 1;
			panel.loadLevel();
		} else if (e.getActionCommand().equals("最终关")) {
			panel.level = MainPanel.MAX_LEVEL;
			panel.loadLevel();
		} else if (e.getActionCommand().equals("悔一步")) {
			panel.regret();
			panel.repaint();
		} else {
			try {
				throw new Exception("unhandled command");
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}

	private ActionListener listenMusicState = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			panel.requestFocus();
			setMusicState(!musicEnabled);
		}
	};

	void setMusicState(boolean state) {
		musicEnabled = state;
		musicComboBox.setVisible(musicEnabled);
		lb2.setVisible(musicEnabled);
		musicMenu.setEnabled(musicEnabled);
		musicCheckBox.setSelected(musicEnabled);
		musicCheckecBoxMenuItem.setSelected(musicEnabled);
		if (musicEnabled)
			sound.loadSound();
		else
			sound.stop();
	}

	public void itemStateChanged(ItemEvent ie) {
		panel.requestFocus();
		String s = (ie.getItem() instanceof String) ? (String) ie.getItem()
				: ((JRadioButtonMenuItem) ie.getSource()).getText();
		if (sound.file.equals(s))
			return;
		sound.setMusic(s);
		if (sound.isPlay)
			sound.stop();
		sound.loadSound();
		for (int i = 0; i < musics.length; i++) {
			JRadioButtonMenuItem item = (JRadioButtonMenuItem) musicMenu.getItem(i);
			if (item.getActionCommand().equals(s)) {
				item.setEnabled(false);
			} else {
				item.setEnabled(true);
			}
		}
	}
}

class MainPanel extends JPanel {
	static final int MAX_LEVEL = 50;
	static final int SPACE = 0, WALL = 1, GROUND = 2, BOX = 3, HOME = 4;
	static final int DOWN = 5, LEFT = 6, RIGHT = 7, UP = 8, REACH = 9;
	static final int GRID = 30;
	int[][] obj = new int[20][20], terrain = new int[20][20];
	int man;
	Point p = new Point();
	static Image[] imgs;
	int level = 1;
	private Stack<Integer> mystack = new Stack<>();
	int[] minStepRecord = new int[MAX_LEVEL];
	File recordFile = new File("data" + File.separator + "minStepRecord.txt");

	MainPanel() {
		setBounds(15, 50, 600, 600);
		addKeyListener(listenKey);
		imgs = new Image[10];
		for (int i = 0; i < 10; i++) {
			imgs[i] = Toolkit.getDefaultToolkit().getImage("pic\\" + i + ".gif");
		}
		if (recordFile.exists()) {
			Scanner cin;
			try {
				cin = new Scanner(recordFile);
				for (int i = 0; i < MAX_LEVEL; i++)
					minStepRecord[i] = cin.nextInt();
				cin.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	public void save() {
		try {
			PrintWriter cout = new PrintWriter(recordFile);
			for (int i = 0; i < MAX_LEVEL; i++)
				cout.println(minStepRecord[i]);
			cout.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	void loadLevel() {
		Scanner cin = null;
		try {
			cin = new Scanner(new File("maps" + File.separator + level + ".map"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		for (int i = 0; i < 20; i++) {
			String s = cin.next();
			for (int j = 0; j < 20; j++) {
				int k = s.charAt(j) - '0';
				if (k == 5) {
					p = new Point(i, j);
					man = 5;
					terrain[i][j] = GROUND;
					obj[i][j] = -1;
				} else if (k == 3) {
					obj[i][j] = k;
					terrain[i][j] = GROUND;
				} else if (k == 9) {
					obj[i][j] = BOX;
					terrain[i][j] = HOME;
				} else {
					obj[i][j] = -1;
					terrain[i][j] = k;
				}
			}
		}
		cin.close();
		mystack.clear();
		repaint();
	}

	public void paint(Graphics g) {
		for (int i = 0; i < 20; i++)
			for (int j = 0; j < 20; j++) {
				int k = (obj[i][j] == -1) ? terrain[i][j] : obj[i][j];
				if (obj[i][j] == BOX && terrain[i][j] == HOME)
					k = REACH;
				g.drawImage(imgs[k], j * GRID, i * GRID, this);
			}
		g.drawImage(imgs[man], p.y * GRID, p.x * GRID, this);
		g.setColor(Color.BLACK);
		g.setFont(new Font("serif", Font.BOLD, 30));
		String record = minStepRecord[level - 1] == 0 ? "无" : minStepRecord[level - 1] + "步";
		g.drawString(String.format("第 %3d 关,第%3d 步,最高纪录%3s", level, mystack.size(), record), 150, 40);
	}

	KeyListener listenKey = new KeyAdapter() {
		final int[] code = new int[] { KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_UP };

		public void keyPressed(KeyEvent e) {
			int direction = -1;
			for (int i = 0; i < 4; i++) {
				if (code[i] == e.getKeyCode()) {
					direction = i;
				}
			}
			if (direction == -1) {
				if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
					regret();
					repaint();
				}
			} else {
				move(direction);
				repaint();
				if (iswin()) {
					win();
				}
			}
		}
	};

	void win() {
		int history = minStepRecord[level - 1];
		boolean createRecord = (history == 0 || history > mystack.size());
		String recordStr = "最高纪录" + history + "步";
		if (createRecord) {
			minStepRecord[level - 1] = mystack.size();
			recordStr = history == 0 ? "您创造记录" : "您打破了记录" + history + "步";
		}
		JOptionPane.showMessageDialog(MainPanel.this,
				String.format("恭喜您用%d步通过第%d关!!!\n%s", mystack.size(), level, recordStr));
		if (level == MAX_LEVEL) {
			JOptionPane.showMessageDialog(MainPanel.this, "这已经是最后一关了");
		} else {
			level++;
			loadLevel();
		}
	}

	int dir[] = new int[] { 1, 0, 0, -1, 0, 1, -1, 0 };

	void move(int direction) {
		man = 5 + direction;
		int x = p.x + dir[direction * 2], y = p.y + dir[direction * 2 + 1];
		if (terrain[x][y] == WALL)
			return;
		else if (obj[x][y] == BOX) {
			int xx = x + dir[direction * 2], yy = y + dir[direction * 2 + 1];
			if (terrain[xx][yy] == WALL || obj[xx][yy] == BOX)
				return;
			else {
				p = new Point(x, y);
				obj[x][y] = -1;
				obj[xx][yy] = BOX;
				mystack.push(4 + direction);
			}
		} else {
			p = new Point(x, y);
			mystack.push(direction);
		}
	}

	void regret() {
		if (mystack.isEmpty())
			return;
		int d = mystack.pop();
		if (d >= 4) {
			d -= 4;
			int x = p.x + dir[d * 2], y = p.y + dir[d * 2 + 1];
			obj[x][y] = -1;
			obj[p.x][p.y] = BOX;
		}
		p.x -= dir[d * 2];
		p.y -= dir[d * 2 + 1];
	}

	boolean iswin() {
		for (int i = 0; i < 20; i++)
			for (int j = 0; j < 20; j++) {
				if (obj[i][j] == BOX && terrain[i][j] != HOME) {
					return false;
				}
			}
		return true;
	}
}

class Sound {
	String path = new String("musics\\");
	String file = "默认";
	Sequence seq;
	Sequencer midi;
	boolean isPlay;

	void loadSound() {
		try {
			seq = MidiSystem.getSequence(new File(path + file + ".mid"));
			midi = MidiSystem.getSequencer();
			midi.open();
			midi.setSequence(seq);
			midi.start();
			midi.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		isPlay = true;
	}

	void stop() {
		midi.stop();
		isPlay = false;
	}

	void setMusic(String e) {
		file = e;
	}
}