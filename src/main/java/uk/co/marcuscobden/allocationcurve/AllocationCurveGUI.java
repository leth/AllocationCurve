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

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.filechooser.FileNameExtensionFilter;

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

	public AllocationCurveGUI()
	{
		super("AllocationCurve");

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
		JSpinner recursion = new JSpinner();
		recursion.setValue(0);
		recursion.setEditor(new JSpinner.NumberEditor(recursion));
		spinnerPanel.add(new JLabel("Depth limit:"));
		spinnerPanel.add(recursion);

		JLabel label = new JLabel("Set to zero for infinite depth");
		label.setFont(label.getFont().deriveFont(Font.ITALIC));

		recursionPanel.add(spinnerPanel);
		recursionPanel.add(label);

		JPanel fileLabelPanel = new JPanel();
		fileLabelPanel.add(inputFileLabel);
		fileLabelPanel.add(outputFileLabel);

		JPanel okCancelPanel = new JPanel();
		okButton = new JButton("Ok");
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

	public void actionPerformed(final ActionEvent e)
	{
		Object source = e.getSource();
		if (source == openButton)
		{
			if (inputFile != null)
				fc.setSelectedFile(inputFile);

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
				outname = inputFile.getParent() + File.separator + outname;
				fc.setSelectedFile(new File(outname));
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
				return;

			// TODO trigger rendering
		}
		else if (source == cancelButton)
		{
			setVisible(false);
			dispose();
		}
	}

}
