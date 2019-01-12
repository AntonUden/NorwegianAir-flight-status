package se.Zeeraa;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.swing.BoxLayout;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.Timer;

import org.json.JSONObject;

public class FlightStatus {
	private JFrame f = new JFrame();
	private JPanel p = new JPanel();
	private JLabel speedLabel = new JLabel("Speed: null");
	private JLabel timeLabel = new JLabel("Time remaining: null");
	private JLabel destintionLabel = new JLabel("Destination: /dev/null");
	private JLabel altitudeLable = new JLabel("Altitude: null");
	private JLabel directionLable = new JLabel("Direction: null");
	private JProgressBar progressBar = new JProgressBar(0, 100);
	private JLabel maxSpeedLabel = new JLabel("Max speed: null");

	private int maxSpeed = 0;

	private Timer updateTimer = new Timer(1000, new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			update();
		}
	});

	public FlightStatus() {
		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("Menu");

		JCheckBoxMenuItem aotItem = new JCheckBoxMenuItem();
		JMenuItem exitItem = new JMenuItem();

		aotItem.setText("Always on top");
		aotItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				f.setAlwaysOnTop(aotItem.getState());
			}
		});

		exitItem.setText("Exit");
		exitItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});

		menu.add(aotItem);
		menu.add(exitItem);

		menuBar.add(menu);

		progressBar.setStringPainted(true);
		progressBar.setValue(0);
		progressBar.setString("0%");

		p.setLayout(new BoxLayout(p, BoxLayout.PAGE_AXIS));
		p.add(destintionLabel);
		p.add(speedLabel);
		p.add(maxSpeedLabel);
		p.add(altitudeLable);
		p.add(directionLable);
		p.add(timeLabel);
		p.add(progressBar);

		f.setTitle("NorwegainAir Flight status");
		f.setResizable(true);
		f.setSize(350, 180);
		f.setLocationRelativeTo(null);
		f.add(p);
		f.setVisible(true);
		f.setAlwaysOnTop(false);
		f.setJMenuBar(menuBar);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		updateTimer.start();
		update();
	}

	private void update() {
		try {
			String url = "http://wifi.norwegian.com/flightinfo.json";
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			con.setUseCaches(false);
			con.setDefaultUseCaches(false);
			con.setRequestMethod("GET");
			con.setRequestProperty("User-Agent", "Mozilla/5.0");
			con.setRequestProperty("Pragma", "no-cache");
			con.setRequestProperty("Expires", "0");

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			// Read JSON response
			JSONObject r = new JSONObject(response.toString());
			JSONObject flightinfo = r.getJSONObject("flightinfo");

			int pcent_flt_complete = flightinfo.getInt("pcent_flt_complete");
			String speed = flightinfo.getString("gspdm");
			String timeRemaining = flightinfo.getString("ttgc");
			String destination = flightinfo.getString("dest_city");
			String altitude = flightinfo.getString("altm");
			String direction = flightinfo.getString("hdg");

			progressBar.setValue(pcent_flt_complete);
			progressBar.setString(pcent_flt_complete + "%");

			speedLabel.setText("Speed: " + speed);

			try {
				int s = Integer.parseInt(speed.replace(" km/h", ""));
				if (maxSpeed < s) {
					maxSpeed = s;
					maxSpeedLabel.setText("Max speed: " + maxSpeed + " km/h");
				}
			} catch (Exception ee) {
				ee.printStackTrace();
			}

			timeLabel.setText("Time remaining: " + timeRemaining);

			destintionLabel.setText("Destination: " + destination);

			altitudeLable.setText("Altitude: " + altitude);

			directionLable.setText("Direction: " + direction);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new FlightStatus();
	}
}