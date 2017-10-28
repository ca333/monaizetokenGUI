/************************************************************************************************
 *  _________          _     ____          _           __        __    _ _      _   _   _ ___
 * |__  / ___|__ _ ___| |__ / ___|_      _(_)_ __   __ \ \      / /_ _| | | ___| |_| | | |_ _|
 *   / / |   / _` / __| '_ \\___ \ \ /\ / / | '_ \ / _` \ \ /\ / / _` | | |/ _ \ __| | | || |
 *  / /| |__| (_| \__ \ | | |___) \ V  V /| | | | | (_| |\ V  V / (_| | | |  __/ |_| |_| || |
 * /____\____\__,_|___/_| |_|____/ \_/\_/ |_|_| |_|\__, | \_/\_/ \__,_|_|_|\___|\__|\___/|___|
 *                                                 |___/
 *
 * Copyright (c) 2017 The SuperNET Developers <ca333@supernet.org>
 * Copyright (c) 2016 Ivan Vaklinov <ivan@vaklinov.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 **********************************************************************************/
package com.vaklinov.zcashui;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import javax.xml.bind.DatatypeConverter;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import com.vaklinov.zcashui.Base58;

/**
 * Dialog to enter a iguana seed to import
 *
 * @author Ivan Vaklinov <ivan@vaklinov.com>
 * @author The SuperNET Developers <ca333@supernet.org>
 */
public class SeedImportDialog
	extends JDialog
{
	protected boolean isOKPressed = false;
	protected String  key    = null;

	protected JLabel     keyLabel = null;
	protected JTextField keyField = null;

	protected JLabel upperLabel;
	protected JLabel lowerLabel;

	protected JProgressBar progress = null;

	protected ZCashClientCaller caller;

	JButton okButon;
	JButton cancelButon;

	public SeedImportDialog(JFrame parent, ZCashClientCaller caller)
	{
		super(parent);
		this.caller = caller;

		this.setTitle("Enter Iguana Seed...");
	    this.setLocation(parent.getLocation().x + 50, parent.getLocation().y + 50);
		this.setModal(true);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		JPanel controlsPanel = new JPanel();
		controlsPanel.setLayout(new BoxLayout(controlsPanel, BoxLayout.Y_AXIS));
		controlsPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

		JPanel tempPanel = new JPanel(new BorderLayout(0, 0));
		tempPanel.add(this.upperLabel = new JLabel(
			"<html>Please enter a seed key to import." +
		    "</html>"), BorderLayout.CENTER);
		controlsPanel.add(tempPanel);

		JLabel dividerLabel = new JLabel("   ");
		dividerLabel.setFont(new Font("Helvetica", Font.PLAIN, 8));
		controlsPanel.add(dividerLabel);

		tempPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		tempPanel.add(keyLabel = new JLabel("Seed: "));
		tempPanel.add(keyField = new JTextField(60));
		controlsPanel.add(tempPanel);

		dividerLabel = new JLabel("   ");
		dividerLabel.setFont(new Font("Helvetica", Font.PLAIN, 8));
		controlsPanel.add(dividerLabel);

		tempPanel = new JPanel(new BorderLayout(0, 0));
		tempPanel.add(this.lowerLabel = new JLabel(
			"<html><span style=\"font-weight:bold\">" +
		    "Warning:</span> Seed import is a slow operation that " +
		    "requires blockchain rescanning (may take many minutes). The GUI " +
			"will not be usable for other functions during this time!</html>"),
			BorderLayout.CENTER);
		controlsPanel.add(tempPanel);

		dividerLabel = new JLabel("   ");
		dividerLabel.setFont(new Font("Helvetica", Font.PLAIN, 8));
		controlsPanel.add(dividerLabel);

		tempPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		tempPanel.add(progress = new JProgressBar());
		controlsPanel.add(tempPanel);

		this.getContentPane().setLayout(new BorderLayout(0, 0));
		this.getContentPane().add(controlsPanel, BorderLayout.NORTH);

		// Form buttons
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 3, 3));
		okButon = new JButton("Import");
		buttonPanel.add(okButon);
		buttonPanel.add(new JLabel("   "));
		cancelButon = new JButton("Cancel");
		buttonPanel.add(cancelButon);
		this.getContentPane().add(buttonPanel, BorderLayout.SOUTH);

		okButon.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				SeedImportDialog.this.processOK();
			}
		});

		cancelButon.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				SeedImportDialog.this.setVisible(false);
				SeedImportDialog.this.dispose();

				SeedImportDialog.this.isOKPressed = false;
				SeedImportDialog.this.key = null;
			}
		});

		this.setSize(740, 210);
		this.validate();
		this.repaint();
	}


	protected void processOK()
	{
		final String key = SeedImportDialog.this.keyField.getText();

		if ((key == null) || (key.trim().length() <= 0))
		{
			JOptionPane.showMessageDialog(
				SeedImportDialog.this.getParent(),
				"The seed is empty. Please enter it into the text field.", "Empty...",
				JOptionPane.ERROR_MESSAGE);
			return;
		}

		SeedImportDialog.this.isOKPressed = true;
		SeedImportDialog.this.key = key;

		// Start import
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		this.progress.setIndeterminate(true);
		this.progress.setValue(1);

		this.okButon.setEnabled(false);
		this.cancelButon.setEnabled(false);

		SeedImportDialog.this.keyField.setEditable(false);

		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					String privkey = null;
					//todo: benchmark - org.apache.commons.codec.digest.DigestUtils.sha256Hex(key);
					//MessageDigest md = MessageDigest.getInstance("SHA-256");
					//byte[] hash = md.digest(key.getBytes(StandardCharsets.UTF_8));
					//privkey = byte.toString();
					privkey = seed2Wif(key);
					SeedImportDialog.this.caller.importPrivateKey(privkey);


					JOptionPane.showMessageDialog(
							SeedImportDialog.this,
							"The seed:\n" +
							key + "\n" +
							"has been imported successfully.",
							"Seed imported successfully...",
							JOptionPane.INFORMATION_MESSAGE);
				} catch (Exception e)
				{
					e.printStackTrace();

					JOptionPane.showMessageDialog(
						SeedImportDialog.this.getRootPane().getParent(),
						"An error occurred when importing the seed. Error message is:\n" +
						e.getClass().getName() + ":\n" + e.getMessage() + "\n\n" +
						"Please ensure that komodod is running and the seed is in the correct \n" +
						"format. You may try again later...\n",
						"Error in importing seed", JOptionPane.ERROR_MESSAGE);
				} finally
				{
					SeedImportDialog.this.setVisible(false);
					SeedImportDialog.this.dispose();
				}
			}
		}).start();
	}


	public boolean isOKPressed()
	{
		return this.isOKPressed;
	}


	private static String seed2Wif(String key){

		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		}

		byte[] hash = md.digest(key.getBytes(StandardCharsets.UTF_8));
		hash[0] &= 248;
		hash[31] &= 127;
		hash[31] |= 64;

		byte[] pre = hexStringToByteArray(Integer.toHexString(0xff & 188));
		//byte[] app = hexStringToByteArray(Integer.toHexString(0xff & 1));

		byte[] chash = new byte[hash.length+2];
		//byte[] chash = new byte[hash.length+1];


		for (int i = 0; i < chash.length; ++i)
		{
			chash[i] = (i == 0 ? pre[0] : i == chash.length-1 ? (0xff & 1) : hash[i-1]);
		}
		//System.out.print("PrivKey with pre/app byte: " + bytesToHex(chash) + "\n");

		byte[] doubl = md.digest(md.digest(chash));
		//System.out.println("double sha256:         "+bytesToHex(doubl));

		/*below is stringyfied method - not efficient*/

		String priv = bytesToHex(hash); //
		//System.out.println("PrivKey :" + priv);
		//String extKey = "BC" + priv;
		//String extKey = "BC" + priv + "01";  //bitcoin test
		//byte[] x = hexStringToByteArray(extKey);
		//System.out.print("PrivKey with pre byte: " +bytesToHex(x) + "\n");
		//hash = md.digest(md.digest(x));
		//System.out.println(bytesToHex(hash));

		String privkey = sha256ToPrivKey(doubl, bytesToHex(chash));
		//System.out.println(privkey);

		String wif = encodePrivateKeyToWIF(privkey);
		//System.out.println(wif);

		return wif;
	}


	public static byte[] hexStringToByteArray(String s) {
	   int len = s.length();
	   byte[] data = new byte[len / 2];
	   for (int i = 0; i < len; i += 2) {
	       data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
	                            	+ Character.digit(s.charAt(i+1), 16));
	    }
	    return data;
	}

	private static String stringSha256(String key) {
		try{
				MessageDigest digest = MessageDigest.getInstance("SHA-256");
				byte[] hash = digest.digest(key.getBytes("UTF-8"));

				StringBuffer hexString = new StringBuffer();

				for (int i = 0; i < hash.length; i++) {
						String hex = Integer.toHexString(0xff & hash[i]);
						if(hex.length() == 1) hexString.append('0');
						hexString.append(hex);
				}

				return hexString.toString().toUpperCase();
		} catch(Exception ex){
			 throw new RuntimeException(ex);
		}
	}


	private static String sha256ToPrivKey(byte[] dsha, String sha){

			StringBuffer checksum = new StringBuffer();

			byte[] shash = hexStringToByteArray(sha);
			for(int i=0;i<shash.length;i++){
				String buf = Integer.toHexString(0xff & shash[i]);
					if(buf.length() == 1) checksum.append('0');
				checksum.append(buf);
				//result[i+4] = shash[i];
			}

			byte[] dhash = dsha;
			//System.out.println(bytesToHex(hexStringToByteArray(dsha)));

		for(int i=0;i<4;i++){
			String buf = Integer.toHexString(0xff & dhash[i]);
			if(buf.length() == 1) checksum.append('0');
			checksum.append(buf);
			//result[i] =
		}

		int len = checksum.length();
		//System.out.println(Integer.toString(len/2));

		//System.out.println(String.toHexString(checksum.toString()));
		return checksum.toString().toUpperCase();
	}


	private static String encodePrivateKeyToWIF (String privateKey)
	{
		byte[] priv = hexStringToByteArray(privateKey);
		if (priv[0] == 0)
		{
			priv = Arrays.copyOfRange(priv, 1, priv.length);
		}
		//System.out.println(bytesToHex(priv) + " - PRIV\n");
		String walletImportFormatPrivateKey = Base58.encode(priv);
		return walletImportFormatPrivateKey;
	}

  private static String bytesToHex(byte[] hash) {
    return DatatypeConverter.printHexBinary(hash);
  }

/*
	public String encodePrivateKeyToWIF (byte[] privateKey)
	{
		// If first byte of the private encryption key generated is zero, remove it.
		if (privateKey[0] == 0)
		{
			privateKey = ArrayCopier.copyOfRange(privateKey, 1, privateKey.length);
		}

		byte[] valueToPrepend = new byte[1];
		valueToPrepend[0] = (byte) 128;

		byte[] privateKeyWithExtraByte = ByteUtils.concatenateByteArrays(valueToPrepend, privateKey);
		byte[] hashOfPrivateKey = SHA256.doubleDigest(privateKeyWithExtraByte);
		byte[] checksum = ArrayCopier.copyOfRange(hashOfPrivateKey, 0, 4);
		byte[] convertedPrivateKey = ByteUtils.concatenateByteArrays(privateKeyWithExtraByte, checksum);
		String walletImportFormatPrivateKey = Base58.encode(convertedPrivateKey);

		return walletImportFormatPrivateKey;
}
*/
	public String getKey()
	{
		return this.key;
	}
}
