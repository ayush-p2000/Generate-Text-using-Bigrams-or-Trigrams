import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.io.IOException;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.StringTokenizer;

public class MyLanguageModel extends JFrame implements AdjustmentListener {
    private JTextArea documentTextArea, generateText;
    private JTextArea unigrams;
    private JTextArea bigrams;
    private JTextArea trigrams;
    private JTextArea unigramsCount;
    private JTextArea bigramsCount;
    private JTextArea trigramsCount;
    private nGramAlgorithm gram;
    private File path;
    private JScrollBar uniBar;
    private JScrollBar biBar;
    private JScrollBar triBar;
    private String capturedSentence;
    private JButton filtersort;
    private int ctr=0;
    public MyLanguageModel() throws IOException {

        super("Sequence Generator");
        gram = new nGramAlgorithm();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        

        
//-------------------------------------------- Unigrams and Bigrams Panel ------------------------------------------------------------------------------------//

        Font ft = new Font("Ariel", Font.BOLD, 15);
        JPanel panel = new JPanel(new GridLayout(3,2));

        bigrams = new JTextArea();
        bigrams.setFont(ft);
        bigrams.setText("Bigrams");
        bigrams.setEditable(false);
        JScrollPane biScrollPane = new JScrollPane(bigrams);
        biScrollPane.setPreferredSize(new Dimension(400,250));

        unigrams = new JTextArea();
        unigrams.setFont(ft);
        unigrams.setText("Unigrams");
        unigrams.setEditable(false);
        JScrollPane uniScrollPane = new JScrollPane(unigrams);
        uniScrollPane.setPreferredSize(new Dimension(400,250));

        bigramsCount = new JTextArea();
        bigramsCount.setFont(ft);
        bigramsCount.setText("Bigrams Probability (Scaled by 1000 times)");
        bigramsCount.setEditable(false);
        JScrollPane bCountScrollPane = new JScrollPane(bigramsCount);
        bCountScrollPane.setPreferredSize(new Dimension(400,250));

        unigramsCount = new JTextArea();
        unigramsCount.setEditable(false);
        unigramsCount.setFont(ft);
        unigramsCount.setText("Unigrams Count");
        JScrollPane uCountScrollPane = new JScrollPane(unigramsCount);
        uCountScrollPane.setPreferredSize(new Dimension(400,250));

        trigrams = new JTextArea();
        trigrams.setFont(ft);
        trigrams.setText("Trigrams");
        trigrams.setEditable(false);
        JScrollPane triScrollPane = new JScrollPane(trigrams);
        triScrollPane.setPreferredSize(new Dimension(400,250));

        trigramsCount = new JTextArea();
        trigramsCount.setEditable(false);
        trigramsCount.setFont(ft);
        trigramsCount.setText("Trigrams Probabilty (Scaled by 1000 times)");
        JScrollPane tCountScrollPane = new JScrollPane(trigramsCount);
        tCountScrollPane.setPreferredSize(new Dimension(400,250));


        uniBar = new JScrollBar(JScrollBar.VERTICAL,0,1,0,100);
        uniBar.setUnitIncrement(20);
        uniBar.setBlockIncrement(40);
        uniBar.addAdjustmentListener(new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e)
            {
                int value = uniBar.getValue();
                unigrams.scrollRectToVisible(new Rectangle(0, value, 1,1));
                unigramsCount.scrollRectToVisible(new Rectangle(0,value, 1, 1));
            }
        });

        uniScrollPane.setVerticalScrollBar(uniBar);
        uCountScrollPane.setVerticalScrollBar(uniBar);



        biBar = new JScrollBar(JScrollBar.VERTICAL,0,1,0,100);
        biBar.setUnitIncrement(20);
        biBar.setBlockIncrement(40);
        biBar.addAdjustmentListener(new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e)
            {
                int value = biBar.getValue();
                bigrams.scrollRectToVisible(new Rectangle(0, value, 1,1));
                bigramsCount.scrollRectToVisible(new Rectangle(0,value, 1, 1));
            }
        });

        biScrollPane.setVerticalScrollBar(biBar);
        bCountScrollPane.setVerticalScrollBar(biBar);


        triBar = new JScrollBar(JScrollBar.VERTICAL,0,1,0,100);
        triBar.setUnitIncrement(20);
        triBar.setBlockIncrement(40);
        triBar.addAdjustmentListener(new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e)
            {
                int value = triBar.getValue();
                trigrams.scrollRectToVisible(new Rectangle(0, value, 1,1));
                trigramsCount.scrollRectToVisible(new Rectangle(0,value, 1, 1));
            }
        });

        triScrollPane.setVerticalScrollBar(triBar);
        tCountScrollPane.setVerticalScrollBar(triBar);

        
        panel.add(uniScrollPane);
        panel.add(uCountScrollPane);
        panel.add(biScrollPane);      
        panel.add(bCountScrollPane);
        panel.add(triScrollPane);
        panel.add(tCountScrollPane);


//------------------------------------------ Sequence Panel --------------------------------------------------------------------------------------------------//

        JPanel vocabPanel = new JPanel(new GridLayout(2,1));

        

        generateText = new JTextArea("Generated Sequence");
        generateText.setEditable(false);
        generateText.setFont(ft);
        JScrollPane genTextJScrollPane = new JScrollPane(generateText);

        documentTextArea = new JTextArea("Write two words to generate sequence");
        documentTextArea.setPreferredSize(new Dimension(50,100));
        documentTextArea.setFont(ft);

        JButton generate = new JButton("Generate Sequence");
        generate.setEnabled(false);
        generate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                String[] sentence = documentTextArea.getText().toLowerCase().split("\\s+");
                try {
                    if(initLength(documentTextArea.getText()) <= 3)
                    {
                        capturedSentence = "";
                            //generatedWords.append(gram.display(sentence)+" ");
                           // generateText.setText(gram.display(sentence).toString());
                        for(String word : gram.display(sentence))
                        {
                            if (word!=null) {
                                capturedSentence = capturedSentence+word+" ";
                            }      
                        }
                        generateText.setText(capturedSentence);
                        
                        
                    }
                    else{
                        JOptionPane.showMessageDialog(MyLanguageModel.this, "Please enter two words and try again","Error",JOptionPane.PLAIN_MESSAGE);

                    }

                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        });

        vocabPanel.add(documentTextArea);
        vocabPanel.add(genTextJScrollPane);
        vocabPanel.add(generate);
        

//----------------------------------------------- JFrame Border Layout -----------------------------------------------------------------------------------------------------------// 


JPanel buttonPanel = new JPanel(new GridLayout(1,2));

        JButton file = new JButton("Select File");
        file.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser jfile = new JFileChooser();
                int response = jfile.showOpenDialog(null);
                if (response == JFileChooser.APPROVE_OPTION) {
                    path =new File(jfile.getSelectedFile().getAbsolutePath());
                    generate.setEnabled(true);
                    filtersort.setEnabled(true);
                }
                try {
                    processDocument();
                    }
                 catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        });




        filtersort = new JButton("Toggle Sort Filter");
        filtersort.setEnabled(false);
        filtersort.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ctr+=1;
                if(ctr%2==0)
                {
                    displayUnigramByWord();
                }
                else
                {
                    displayUnigramByCount();
                    
                }
            }
        });

        buttonPanel.add(file);
        buttonPanel.add(filtersort);
    
        add(buttonPanel, BorderLayout.CENTER);       
        add(panel, BorderLayout.NORTH);
        add(vocabPanel, BorderLayout.SOUTH);


        pack();
        setVisible(true);

    }

//---------------------------------------------------------- Reading and Processing Document ----------------------------------------------------------------//

    private void processDocument() throws IOException {
        gram = new nGramAlgorithm();

        Scanner reader = new Scanner(path);
        String documentText = "";

        while(reader.hasNextLine()) 
        {
            documentText = documentText + reader.nextLine();
        }
        reader.close();
        String[] words = documentText.split("\\s+");
        List<String> correctWords = new ArrayList<>();
        List<String> misprocess = new ArrayList<>();
        for (String word : words) {
            // Simple check for symbols and non-lowercase letters
            if (word.matches(".*[^a-z.'].*")) {
                misprocess.add(word);
            } else {
                correctWords.add(word);
                // myHashTable.add(word);
            }
        }
        gram.extract(correctWords);
        if(!misprocess.isEmpty())
            JOptionPane.showMessageDialog(this, "Mis-processed item: " + misprocess.toString(), "Warning", JOptionPane.WARNING_MESSAGE);
        else
             JOptionPane.showMessageDialog(this, "No mis-processed item","Processing Document",JOptionPane.PLAIN_MESSAGE);



        displayUnigramByWord();
        displayTrigramProbabilities();
        displayProbabilities();
    }


    private void displayUnigramByWord() {
        StringBuilder unigram = new StringBuilder();
        StringBuilder uniCount = new StringBuilder();
        List<MyLinkedObject> sortedList = sortListByFrequency();

        for (MyLinkedObject obj : sortedList) {
            if (obj != null) {
                
                //displayText.append(obj.getWord().trim()).append("\t\t\t").append(obj.getCount()).append("\n");
                unigram.append(obj.getWord()+"\n");
                uniCount.append(obj.getCount()+"\n");
            }
        }
        //unigrams.setText("Unigrams"+"\t\t\t"+"Count\n\n"+displayText.toString());
        Font ft = new Font("Ariel", Font.BOLD, 15);

        unigrams.setText("Unigrams \n\n"+unigram.toString());
        unigrams.setFont(ft); 

        unigramsCount.setText("Unigrams Count\n\n"+uniCount.toString());
        unigramsCount.setFont(ft);  
        //unigrams.setText(displayText.toString()); 
    } 

    private void displayUnigramByCount() {
        StringBuilder unigram = new StringBuilder();
        StringBuilder uniCount = new StringBuilder();
        List<MyLinkedObject> sortedList = sortUnigramsByCount();

        for (MyLinkedObject obj : sortedList) {
            if (obj != null) {
                
                //displayText.append(obj.getWord().trim()).append("\t\t\t").append(obj.getCount()).append("\n");
                unigram.append(obj.getWord()+"\n");
                uniCount.append(obj.getCount()+"\n");
            }
        }
        //unigrams.setText("Unigrams"+"\t\t\t"+"Count\n\n"+displayText.toString());
        Font ft = new Font("Ariel", Font.BOLD, 15);

        unigrams.setText("Unigrams \n\n"+unigram.toString());
        unigrams.setFont(ft); 

        unigramsCount.setText("Unigrams Count\n\n"+uniCount.toString());
        unigramsCount.setFont(ft);  
        //unigrams.setText(displayText.toString()); 
    } 

    private List<MyLinkedObject> sortListByFrequency() {
        List<MyLinkedObject> sortedList = new ArrayList<>();
        MyHashTable unig = gram.getUnigrams();

        for (int i = 0; i < unig.getHashtableSize(); i++) {
            MyLinkedObject current = unig.getHashtable()[i];

            while (current != null) {
                sortedList.add(current);
                current = current.getNext();
            }
        }
        Collections.sort(sortedList, new Comparator<MyLinkedObject>() {

            @Override
            public int compare(MyLinkedObject o1, MyLinkedObject o2) {
                // TODO Auto-generated method stub
                return(o1.getWord().compareTo(o2.getWord()));
            }
            
        });

        return sortedList;
    }

    private List<MyLinkedObject> sortUnigramsByCount() {
        List<MyLinkedObject> unigramList = new ArrayList<>();
        MyHashTable unigramHashTable = gram.getUnigrams();
    
        for (int i = 0; i < unigramHashTable.getHashtableSize(); i++) {
            MyLinkedObject current = unigramHashTable.getHashtable()[i];
    
            while (current != null) {
                unigramList.add(current);
                current = current.getNext();
            }
        }
        Collections.sort(unigramList, Comparator.comparingInt(MyLinkedObject::getCount).reversed());
    
        return unigramList;
    }

    
    private void displayProbabilities() {
        Map<String, Double> probMap = gram.getProbabilities();
        //StringBuilder sb = new StringBuilder();
        StringBuilder bigram = new StringBuilder();
        StringBuilder biCount = new StringBuilder();
        for(Map.Entry<String, Double> map : probMap.entrySet()) {
            //sb.append(map.getKey().trim() +"\t\t\t"+map.getValue()+"\n");
            bigram.append(map.getKey()+"\n");
            biCount.append(map.getValue()+"\n");
            
        }
        Font ft = new Font("Ariel", Font.BOLD, 15);

        bigrams.setFont(ft);
        bigrams.setText("Bigrams\n\n"+bigram.toString());

        bigramsCount.setFont(ft);
        bigramsCount.setText("Bigrams Probabilty Scaled by 1000 times\n\n"+biCount.toString());
        System.out.println("1"+bigram.toString()+"    "+biCount.toString());
        //bigrams.setText(sb.toString());
        
    }

    private void displayTrigramProbabilities() {
        Map<String, Double> probMap = gram.getTrigramProbabilities();
        //StringBuilder sb = new StringBuilder();
        StringBuilder bigram = new StringBuilder();
        StringBuilder biCount = new StringBuilder();
        for(Map.Entry<String, Double> map : probMap.entrySet()) {
            //sb.append(map.getKey().trim() +"\t\t\t"+map.getValue()+"\n");
            bigram.append(map.getKey()+"\n");
            biCount.append(map.getValue()+"\n");
            
        }
        Font ft = new Font("Ariel", Font.BOLD, 15);

        trigrams.setFont(ft);
        trigrams.setText("Trigrams\n\n"+bigram.toString());

        trigramsCount.setFont(ft);
        trigramsCount.setText("Trigrams Probabilty Scaled by 1000 times\n\n"+biCount.toString());
        System.out.println("1"+bigram.toString()+"    "+biCount.toString());
        //bigrams.setText(sb.toString());
        
    }

    private int initLength(String str)
    {
        str = str.trim();
        StringTokenizer st = new StringTokenizer(str);
        return st.countTokens();

    }



    

    public static void main(String[] args) {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    new MyLanguageModel();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void adjustmentValueChanged(AdjustmentEvent e) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'adjustmentValueChanged'");
    }
}
