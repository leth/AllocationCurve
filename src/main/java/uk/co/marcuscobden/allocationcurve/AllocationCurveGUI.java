/*
	Copyright: Marcus Cobden (2011)
	This file is part of AllocationCurve.

	AllocationCurve is free software: you can redistribute it and/or modify
	it under the terms of version 3 of the GNU Lesser General Public License
	as published by the Free Software Foundation.

	AllocationCurve is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
	GNU Lesser General Public License for more details.

	You should have received a copy of the GNU Lesser General Public License
	along with AllocationCurve. If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.marcuscobden.allocationcurve;

import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.yaml.snakeyaml.constructor.ConstructorException;
import org.yaml.snakeyaml.error.YAMLException;

public class AllocationCurveGUI extends JFrame
		implements
			ActionListener
{

	private static final long serialVersionUID = 8827520835714027509L;

	private JFileChooser fc;

	private JButton openButton;
	private JButton saveButton;

	private File inputFile;
	private File outputFile;

	private JLabel inputFileLabel;
	private JLabel outputFileLabel;

	private JButton okButton;
	private JButton cancelButton;

	private JSpinner depthLimitSpinner;

	public AllocationCurveGUI(AllocationCurveOptions opts)
	{
		super("AllocationCurve");
		
		setupGUI();
		
		inputFile = opts.input;
		outputFile = opts.output;
		
		if (inputFile != null)
		{
			inputFileLabel.setText(inputFile.getName());
		}
		if (outputFile != null)
		{
			outputFileLabel.setText(outputFile.getName());
		}
		
		depthLimitSpinner.setValue(opts.depthLimit);
	}
	
	protected void setupGUI()
	{
		fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);

		Container content = getContentPane();
		content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

		openButton = new JButton("Select input file...");
		openButton.addActionListener(this);

		saveButton = new JButton("Select output file...");
		saveButton.addActionListener(this);

		JPanel buttonPanel = new JPanel();
		buttonPanel.add(openButton);
		buttonPanel.add(saveButton);

		inputFileLabel = new JLabel("no file selected");
		outputFileLabel = new JLabel("no file selected");

		JPanel recursionPanel = new JPanel();
		recursionPanel
				.setLayout(new BoxLayout(recursionPanel, BoxLayout.Y_AXIS));

		JPanel spinnerPanel = new JPanel();
		depthLimitSpinner = new JSpinner();
		
		depthLimitSpinner
				.setEditor(new JSpinner.NumberEditor(depthLimitSpinner));
		spinnerPanel.add(new JLabel("Depth limit:"));
		spinnerPanel.add(depthLimitSpinner);

		JLabel label = new JLabel("Set to -1 for infinite depth");
		label.setFont(label.getFont().deriveFont(Font.ITALIC));

		recursionPanel.add(spinnerPanel);
		recursionPanel.add(label);

		JPanel fileLabelPanel = new JPanel();
		fileLabelPanel.add(inputFileLabel);
		fileLabelPanel.add(outputFileLabel);

		JPanel okCancelPanel = new JPanel();
		okButton = new JButton("Render");
		okButton.addActionListener(this);
		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(this);

		okCancelPanel.add(okButton);
		okCancelPanel.add(cancelButton);

		content.add(buttonPanel);
		content.add(fileLabelPanel);
		content.add(recursionPanel);
		content.add(okCancelPanel);

		pack();
	}

	public void actionPerformed(final ActionEvent event)
	{
		Object source = event.getSource();
		if (source == openButton)
		{
			if (inputFile != null)
				fc.setSelectedFile(inputFile);
			else
				fc.setSelectedFile(new File(System.getProperty("user.dir")));

			fc.setFileFilter(new FileNameExtensionFilter(
					"Allocation files (txt, yaml)", "txt", "text", "yml",
					"yaml"));
			int returnVal = fc.showOpenDialog(this);

			if (returnVal == JFileChooser.APPROVE_OPTION)
			{
				inputFile = fc.getSelectedFile();
				inputFileLabel.setText(inputFile.getName());
			}
		}
		else if (source == saveButton)
		{
			// Guess what the output file should be
			if (inputFile != null)
			{
				String outname = inputFile.getName();
				outname = outname.substring(0, outname.lastIndexOf('.'))
						+ ".svg";
				fc.setSelectedFile(new File(inputFile.getParentFile(), outname));
			}

			fc.setFileFilter(new FileNameExtensionFilter("SVG file (svg)",
					"svg"));
			int returnVal = fc.showSaveDialog(this);

			if (returnVal == JFileChooser.APPROVE_OPTION)
			{
				outputFile = fc.getSelectedFile();
				outputFileLabel.setText(outputFile.getName());
			}
		}
		else if (source == okButton)
		{
			if (inputFile == null || outputFile == null)
			{
				JOptionPane.showMessageDialog(this,
						"Input/Output files not specified.", "AllocationCurve",
						JOptionPane.WARNING_MESSAGE);
				return;
			}

			FileInputStream input;
			try
			{
				input = new FileInputStream(inputFile);
			} catch (FileNotFoundException e)
			{
				JOptionPane.showMessageDialog(this,
						"File not found\n" + e.getMessage(), "AllocationCurve",
						JOptionPane.ERROR_MESSAGE);
				return;
			}

			AllocationRecord root;
			try
			{
				root = AllocationCurveMain.loadConfig(input,
						inputFile.getParentFile());
			} catch (ConstructorException e)
			{
				Throwable foo = e;
				while (foo instanceof YAMLException)
				{
					foo = foo.getCause();
				}
				JOptionPane
						.showMessageDialog(
								this,
								"Error in allocation declaration:\n"
										+ foo.getMessage(), "AllocationCurve",
								JOptionPane.ERROR_MESSAGE);
				return;
			}

			FileOutputStream output;
			try
			{
				output = new FileOutputStream(outputFile);
			} catch (FileNotFoundException e)
			{
				JOptionPane.showMessageDialog(this, "Cannot write to file. \n"
						+ e.getMessage(), "AllocationCurve",
						JOptionPane.ERROR_MESSAGE);
				return;
			}

			try
			{
				AllocationCurveMain.render(output, root,
						(Integer) depthLimitSpinner.getValue());
			} catch (Exception e)
			{
				JOptionPane.showMessageDialog(this,
						"Error rendering allocation. \n" + e.getMessage(),
						"AllocationCurve", JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
				return;
			}
			JOptionPane.showMessageDialog(this, "Rendering complete.",
					"AllocationCurve", JOptionPane.PLAIN_MESSAGE);
		}
		else if (source == cancelButton)
		{
			setVisible(false);
			dispose();
		}
	}

}
