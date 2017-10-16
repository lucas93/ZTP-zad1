import java.io.BufferedReader;
import java.io.FileReader;

/**
 * @author Lukasz Drozdz
 *
 */
public class AppClient
{
  public static void main(String[] args)
  {
    AppClient app = new AppClient(args[0], args[1]);

    System.out.println("Linia : " + app.getLineWithShortestDistance());
  }

  private AppClient(final String filename, final String pattern)
  {
    this.filename = filename;
    this.pattern = prepareString(pattern);
    levenshteins = getLevenshteins(this.pattern);
  }
  
  private final String filename;
  private final String pattern;
  private int minDistance = Integer.MAX_VALUE;
  private int minDistanceLine = Integer.MAX_VALUE;
  private Levenshtein[] levenshteins;

  private class Line
  {
	Line(final int number, final String str)
	{
	  this.number = number;
	  this.str = str;
	}
		  
    int number;
	String str = "";
  }

  private String prepareString(final String str)
  {
    String result = str.trim();
    result = result.replaceAll("\\s+", " ");
    result = result.toLowerCase();
    
    return result;
  }

  private Levenshtein[] getLevenshteins(final String pattern)
  {
    final String[] testedPatterns = getTestedPatterns(pattern);
    Levenshtein[] levs = new Levenshtein[testedPatterns.length];
    
    for (int i = 0; i < levs.length; i++)
    {
      levs[i] = new Levenshtein(testedPatterns[i]);
    }
    
    return levs;
  }
  
  private String[] getTestedPatterns(final String pattern)
  {
    final String[] testedWords = pattern.split(" ");
    
    if (testedWords.length != 2 && testedWords.length != 3)
      throw new IllegalArgumentException("Pattern must be 2 or 3 words!");
    
    return getTestedPatternsCombinations(testedWords);
  }
  
  private String[] getTestedPatternsCombinations(final String[] testedWords)
  {
    String[] testedPatterns = new String[testedWords.length];
    
    if (2 == testedPatterns.length)
    {
      testedPatterns[0] = testedWords[0] + " " + testedWords[1];
      testedPatterns[1] = testedWords[1] + " " + testedWords[0];
    }
    else if (3 == testedPatterns.length)
    {
      testedPatterns[0] = 
        testedWords[0] + " " + testedWords[1] + " " + testedWords[2];
      testedPatterns[1] = 
        testedWords[1] + " " + testedWords[2] + " " + testedWords[0];
      testedPatterns[2] = 
        testedWords[2] + " " + testedWords[0] + " " + testedWords[1];
    }
    
    return testedPatterns;
  }
  
  private int getLineWithShortestDistance()
  {
    try (BufferedReader br = new BufferedReader(new FileReader(filename)))
    {
	  int idx = 1;
      for (String line; (line = br.readLine()) != null;)
      {
        processLine(new Line(idx, line));
		++idx;
      }
    } catch (Exception e)
    {
      System.out.println("Exception during file parsing!");
    }
    
    return minDistanceLine;
  }

  private void processLine(Line line)
  {
    if (null == line.str || "" == line.str )
      return;
    
    line.str = prepareString(line.str);
    
    for (int i = 0; i < levenshteins.length; ++i)
    {
      final int newDistance = 
        levenshteins[i].calculateDistance(line.str, minDistance);
      
      minDistance = Math.min(minDistance, newDistance);
	  
	  if (newDistance == minDistance)
	    minDistanceLine = line.number;
    }
  }
}
