package com.shawlyne.mc.MenuMetaMod;

import org.getspout.spoutapi.event.screen.ButtonClickEvent;
import org.getspout.spoutapi.event.screen.ScreenListener;
import org.getspout.spoutapi.event.screen.TextFieldChangeEvent;

public class QuestionListener extends ScreenListener {
	
	public QuestionListener()
	{
		super();
	}
	
	public void onTextFieldChange(TextFieldChangeEvent event) 
	{
		System.out.println("onTextFieldChange text " + ValueMenu.inputTextFields.get( event.getPlayer() ).getText());
		
    }
	
	public void onButtonClick(ButtonClickEvent event) 
	{
		if( event.getButton().getText().equals("Send") )
		{
			if( ValueMenu.inputTextFields.get( event.getPlayer() ) != null )
			{
				System.out.println("inputTextField text " + ValueMenu.inputTextFields.get( event.getPlayer() ).getText());
				MenuMetaModPlayerManager.onPlayerResponse(event.getPlayer(), ValueMenu.inputTextFields.get( event.getPlayer() ).getText());
			}
		}
    }

}
