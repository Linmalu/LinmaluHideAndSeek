package com.linmalu.hideandseek.Data;

public enum GameType
{
	진짜, 블럭, 랜덤;

	public static GameType getGameType(String type)
	{
		if(type.equals(진짜.toString()))
		{
			return 진짜;
		}
		else if(type.equals(블럭.toString()))
		{
			return 블럭;
		}
		else if(type.equals(랜덤.toString()))
		{
			return 랜덤;
		}
		else
		{
			return null;
		}
	}
}
