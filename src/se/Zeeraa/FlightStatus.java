package se.Zeeraa;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.Timer;

import org.json.JSONObject;

public class FlightStatus {
	public JFrame f = new JFrame();
	public JPanel p = new JPanel();
	public JLabel speedLabel = new JLabel("Speed: 0 km/h");
	public JLabel timeLabel = new JLabel("Time remaining: 13 H 37 M");
	public JLabel destintionLabel = new JLabel("Destination: /dev/null");
	public JLabel altitudeLable = new JLabel("Altitude: 0 m");
	public JProgressBar progressBar = new JProgressBar(0, 100);

	public Timer updateTimer = new Timer(1000, new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			update();
		}
	});

	public FlightStatus() {
		progressBar.setStringPainted(true);
		progressBar.setValue(0);
		progressBar.setString("0%");
		
		p.setLayout(new BoxLayout(p, BoxLayout.PAGE_AXIS));
		p.add(destintionLabel);
		p.add(speedLabel);
		p.add(altitudeLable);
		p.add(timeLabel);
		p.add(progressBar);

		f.setTitle("NorwegainAir Flight status");
		f.setResizable(true);
		f.setSize(350, 126);
		f.setLocationRelativeTo(null);
		f.add(p);
		f.setVisible(true);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		updateTimer.start();
		update();
	}

	private void update() {
		try {
			String url = "http://wifi.norwegian.com/flightinfo.json";
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			
			con.setUseCaches( false );
			con.setDefaultUseCaches( false );
			con.setRequestMethod("GET");
			con.setRequestProperty("User-Agent", "Mozilla/5.0");
			con.setRequestProperty( "Pragma",  "no-cache" );
		    con.setRequestProperty( "Expires",  "0" );
		    
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
			
			progressBar.setValue(pcent_flt_complete);
			progressBar.setString(pcent_flt_complete + "%");
			
			speedLabel.setText("Speed: " + speed);
			
			timeLabel.setText("Time remaining: " + timeRemaining);
			
			destintionLabel.setText("Destination: " + destination);
			
			altitudeLable.setText("Altitude: " + altitude);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new FlightStatus();
	}
}