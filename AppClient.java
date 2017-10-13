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

  private final String filename;
  private final String pattern;
  private int minDistance = Integer.MAX_VALUE;
  private Levenshtein[] levenshteins;

  private AppClient(final String filename, final String pattern)
  {
    this.filename = filename;
    this.pattern = prepareString(pattern);
    levenshteins = getLevenshteins(this.pattern);
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
      for (String line; (line = br.readLine()) != null;)
      {
        processLine(line);
      }
    } catch (Exception e)
    {
      System.out.println("Exception during file parsing!");
    }
    
    return minDistance;
  }

  private void processLine(String line)
  {
    if (null == line || "" == line )
      return;
    
    line = prepareString(line);
    
    for (int i = 0; i < levenshteins.length; ++i)
    {
      final int newDistance = 
        levenshteins[i].calculateDistance(line, minDistance);
      
      minDistance = Math.min(minDistance, newDistance);
    }
  }
}
