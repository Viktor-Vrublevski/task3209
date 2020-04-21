package com.javarush.task.task32.task3209;

import com.javarush.task.task32.task3209.listeners.UndoListener;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.io.*;

public class Controller  {
    private View view;
   private HTMLDocument document;
   private File currentFile;

    public static void main(String[] args) {
        View view = new View();
        Controller controller = new Controller(view);
        view.setController(controller);
        view.init();
        controller.init();
    }
    public Controller(View view) {
        this.view = view;
    }
    public void init(){
        createNewDocument();
    }
    public void exit(){
        System.exit(0);
    }


    public HTMLDocument getDocument() {
        return document;
    }

    public void resetDocument(){
        UndoListener listener = view.getUndoListener();
        if (document != null) {
            document.removeUndoableEditListener(listener);
        }
        document = (HTMLDocument) new HTMLEditorKit().createDefaultDocument();
        document.addUndoableEditListener(listener);
        view.update();
    }
    public void setPlainText(String text){
        resetDocument();
        StringReader reader = new StringReader(text);
        HTMLEditorKit kit = new HTMLEditorKit();
        try {
           kit.read(reader,document,0);
        }catch (Exception e){
            ExceptionHandler.log(e);
        }
    }
    public  String getPlainText(){
        StringWriter writer = new StringWriter();
        HTMLEditorKit kit = new HTMLEditorKit();
        try {
            kit.write(writer,document,0,document.getLength());
        }catch (Exception e){
            ExceptionHandler.log(e);
        }
        return writer.toString();
    }
    public void createNewDocument(){
        view.selectHtmlTab();
        resetDocument();
        view.setTitle("HTML редактор");
        view.resetUndo();
        currentFile = null;
    }
    public void openDocument(){
        try {
            view.selectHtmlTab();
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new HTMLFileFilter());
            fileChooser.setDialogTitle("Open File");
            int result = fileChooser.showOpenDialog(view);

            if (result == 0) {

                currentFile = fileChooser.getSelectedFile();
                resetDocument();
                view.setTitle(currentFile.getName());

               FileReader reader = new FileReader(currentFile);
               new HTMLEditorKit().read(reader, document, 0);
               view.resetUndo();
            }
        } catch (Exception e) {
            ExceptionHandler.log(e);
        }
    }
    public void saveDocument(){
        view.selectHtmlTab();
        if (currentFile == null)
            saveDocumentAs();
        else {
            try {
                view.setTitle(currentFile.getName());
                FileWriter writer = new FileWriter(currentFile);
                new HTMLEditorKit().write(writer, document, 0, document.getLength());
            } catch (Exception e) {
                ExceptionHandler.log(e);
            }
        }

    }
    public void saveDocumentAs(){
        view.selectHtmlTab();
        JFileChooser jFileChooser = new JFileChooser();
        jFileChooser.setFileFilter(new HTMLFileFilter());
        jFileChooser.setDialogTitle("Save File");
        int index = jFileChooser.showSaveDialog(view);
        if(index == JFileChooser.APPROVE_OPTION){
            currentFile = jFileChooser.getSelectedFile();
            view.setTitle(currentFile.getName());
            try {
                FileWriter fileWriter = new FileWriter(currentFile);
                new HTMLEditorKit().write(fileWriter, document, 0, document.getLength());
                fileWriter.close();
               /* JOptionPane.showMessageDialog(view,
                        "Файл '" + jFileChooser.getSelectedFile() +
                                " сохранен");*/

            } catch (IOException | BadLocationException e) {
                ExceptionHandler.log(e);
            }
        }

    }

}
