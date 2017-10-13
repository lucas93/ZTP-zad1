/**
 * @author Lukasz Drozdz
 *
 */
class Levenshtein
{
  private static final int COST_DELETE = 1;
  private static final int COST_INSERT = 1;
  private static final int COST_SUBSTITUTE = 1;
  private static final int DEFAULT_THRESHOLD = Integer.MAX_VALUE;
  public static final int MAX_DISTANCE_EXCEEDED = Integer.MAX_VALUE;

  private String pattern = "";
  private String input;
  private int[] vecPrev, vecCurrent;
  private int threshold = DEFAULT_THRESHOLD;
  private int rowMin = Integer.MAX_VALUE;

  public Levenshtein(final String pattern)
  {
    this.pattern = pattern;
  }

  public int calculateDistance(
    final String input, final Integer... threshold)
  {
    this.input = input;
    this.threshold = ( threshold.length > 0 ? 
      threshold[0] : DEFAULT_THRESHOLD );
    
    return calc();
  }
  

  private int calc()
  {
    if (isTrivialResult())
      return trivialResult();

    initializeVectors();
    return calculateLoop();
  }

  private boolean isTrivialResult()
  {
    return pattern.equals(input) || 
           0 == pattern.length() || 
           0 == input.length();
  }

  private int trivialResult()
  {
    if (pattern.equals(input))
    {
      return 0;
    }
    if (pattern.length() == 0)
    {
      return input.length();
    } else
    {
      return pattern.length();
    }
  }

  private void initializeVectors()
  {
    vecPrev = new int[input.length() + 1];
    vecCurrent = new int[input.length() + 1];

    for (int i = 0; i < vecPrev.length; i++)
    {
      vecPrev[i] = i;
    }
  }

  private int calculateLoop()
  {
    for (int i = 0; i < pattern.length(); ++i)
    {
      vecCurrent[0] = (i + 1) * COST_DELETE;
      rowMin = Integer.MAX_VALUE;
      
      calculateRow(i);
      
      if (threshold < rowMin)
        return MAX_DISTANCE_EXCEEDED;
      
      updatePrevVec();
    }
    
    return vecPrev[input.length()];
  }
  
  private void calculateRow(final int idx)
  {
    for (int j = 0; j < input.length(); ++j)
    {
      final int currentCostSubstitute = 
        (pattern.charAt(idx) != input.charAt(j) ? 
          COST_SUBSTITUTE : 0);

      vecCurrent[j + 1] = min3(
        vecCurrent[j] + COST_INSERT,
        vecPrev[j + 1] + COST_DELETE,
        vecPrev[j] + currentCostSubstitute);
  
      rowMin = Math.min(rowMin, vecCurrent[j + 1]);
    }
  }

  int min3(final int a, final int b, final int c)
  {
    return Math.min(a, Math.min(b, c));
  }

  private void updatePrevVec()
  {
    int[] vecTemp = vecPrev;
    vecPrev = vecCurrent;
    vecCurrent = vecTemp;
  }
}