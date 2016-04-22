package ru.popularsinger.sergeyd.popularsingers.Common;

/**
 * Created by sergeyd on 04/05/2016.
 */
public class RightEnding
{
    public static String getString(int num, String nominative, String genitive, String plural)
    {
        String result;
        if ( num % 10 == 1 && num % 100 != 11 )
            result = nominative;
        else if ( num % 10 >= 2 && num % 10 <= 4 && (num % 100 < 10 || num % 100 >= 20) )
            result = genitive;
        else
            result = plural;

        return result;
    }
}
