package com.shawlyne.mc.MenuMetaMod;

import org.getspout.spoutapi.event.screen.ButtonClickEvent;
import org.getspout.spoutapi.event.screen.ScreenListener;

public class QuestionListener extends ScreenListener {
	
	public QuestionListener()
	{
		super();
	}
	
	/*public void onTextFieldChange(TextFieldChangeEvent event) 
	{
		System.out.println("onTextFieldChange text " + event.getNewText());
		if( ValueMenu.inputTextValues.get( event.getPlayer() ) != null )
		{
			System.out.println("Updated text " + event.getNewText());
			ValueMenu.inputTextValues.put(event.getPlayer(), event.getNewText());
		}
    }*/
	
	public void onButtonClick(ButtonClickEvent event) 
	{
		if( event.getButton().getText().equals("Send") )
		{
			if( ValueMenu.inputTextFields.get( event.getPlayer() ) != null )
			{
				System.out.println("Updated text " + ValueMenu.inputTextFields.get( event.getPlayer() ).getText());
				MenuMetaModPlayerManager.onPlayerResponse(event.getPlayer(), ValueMenu.inputTextFields.get( event.getPlayer() ).getText());
			}
		}
    }

}
