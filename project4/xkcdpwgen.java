import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class xkcdpwgen {
  static String helpTxt = "usage: xkcdpwgen [-h] [-w WORDS] [-c CAPS] [-n NUMBERS] [-s SYMBOLS]\n"
      + "                \n" + "Generate a secure, memorable password using the XKCD method\n"
      + "                \n" + "optional arguments:\n"
      + "    -h, --help            show this help message and exit\n"
      + "    -w WORDS, --words WORDS\n"
      + "                          include WORDS words in the password (default=4)\n"
      + "    -c CAPS, --caps CAPS  capitalize the first letter of CAPS random words\n"
      + "                          (default=0)\n" + "    -n NUMBERS, --numbers NUMBERS\n"
      + "                          insert NUMBERS random numbers in the password\n"
      + "                          (default=0)\n" + "    -s SYMBOLS, --symbols SYMBOLS\n"
      + "                          insert SYMBOLS random symbols in the password\n"
      + "                          (default=0)";

  public static void main(String[] args) throws IOException {
    if (new Utils().stringArrayContains("-h", args)
        || new Utils().stringArrayContains("--help", args)) {
      System.out.println(helpTxt);
    }
    else {
      Pwgen gen = new Pwgen();
      if ((args.length % 2) != 0) {
        System.out.println("Missing argument parameter!");
      }
      else {
        gen.parseArgs(args);
        System.out.println("Password: " + gen.makePassword());
      }
    }
  }
}

class Pwgen {
  int words = 4;
  int caps = 0;
  int numbers = 0;
  int symbols = 0;
  String[] possibleSymbols = { "~", "!", "@", "#", "$", "%", "^", "&", "*", ".", ":", ";" };

  void parseArgs(String[] args) {
    try {
      for (int i = 0; i < args.length; i++) {
        switch (args[i]) {
        case "-w":
          this.words = Integer.parseInt(args[i + 1]);
          break;
        case "--words":
          this.words = Integer.parseInt(args[i + 1]);
        case "-c":
          this.caps = Integer.parseInt(args[i + 1]);
          if (this.caps > this.words) {
            this.caps = this.words;
          }
          break;
        case "--caps":
          this.caps = Integer.parseInt(args[i + 1]);
          if (this.caps > this.words) {
            this.caps = this.words;
          }
        case "-n":
          this.numbers = Integer.parseInt(args[i + 1]);
          break;
        case "--numbers":
          this.numbers = Integer.parseInt(args[i + 1]);
        case "-s":
          this.symbols = Integer.parseInt(args[i + 1]);
          break;
        case "--symbols":
          this.symbols = Integer.parseInt(args[i + 1]);
        }
      }
    }
    catch (Exception e) {
      System.out.println("Bad arguments!");
    }
  }

  String makePassword() throws IOException {
    String[] wordList = getRandomWords(this.words);
    System.out.println(Arrays.toString(wordList));
    wordList = selectRandWords(wordList);
    wordList = addNums(wordList);
    wordList = addSymbols(wordList);
    return new Utils().stringify(wordList);
  }

  String[] addSymbols(String[] wordList) {
    Random rand = new Random();
    String[] wl = wordList;
    for (int i = 0; i < this.symbols; i++) {
      int randIdx = rand.nextInt(this.possibleSymbols.length - 1);
      wl = addPos(wl, this.possibleSymbols[randIdx], rand.nextInt(wl.length));
    }
    return wl;
  }

  String[] addNums(String[] wordList) {
    String[] wl = wordList;
    Random rand = new Random();
    for (int i = 0; i < this.numbers; i++) {
      int randIdx = rand.nextInt(wl.length + 1);
      wl = addPos(wl, Integer.toString(rand.nextInt(10)), randIdx);
    }
    return wl;
  }

//Function to insert x in arr at position pos
  String[] addPos(String[] a, String str, int pos) {
    String[] result = new String[a.length + 1];
    for (int i = 0; i < pos; i++)
      result[i] = a[i];
    result[pos] = str;
    for (int i = pos + 1; i < a.length + 1; i++)
      result[i] = a[i - 1];
    return result;
  }

  String[] selectRandWords(String[] wordList) {
    Random rand = new Random();
    String[] wl = wordList;
    int[] idxList = new int[wordList.length];
    Arrays.setAll(idxList, i -> i + 1);
    for (int i = 0; i < this.caps; i++) {
      int randIdx = rand.nextInt(idxList.length);
      wl[randIdx] = wl[randIdx].substring(0, 1).toUpperCase() + wl[randIdx].substring(1);
      idxList = removeElementAtIndex(randIdx, idxList);
    }
    return wl;
  }

  int[] removeElementAtIndex(int idx, int[] array) {
    int index = idx;

    // create an array to hold elements after deletion
    int[] copyArray = new int[array.length - 1];

    // copy elements from original array from beginning till index into copyArray
    System.arraycopy(array, 0, copyArray, 0, index);

    // copy elements from original array from index+1 till end into copyArray
    System.arraycopy(array, index + 1, copyArray, index, array.length - index - 1);
    return copyArray;
  }

  String[] getRandomWords(int len) throws IOException {
    // ClassLoader classLoader = getClass().getClassLoader();
    InputStream inputStream = Pwgen.class.getResourceAsStream("words.txt");
    ArrayList<String> wordList = new ArrayList<String>();
    try {
      wordList = readFromInputStream(inputStream);
    }
    catch (IOException e) {
      // TODO Auto-generated catch block
      System.out.println("Error reading file!");
    }
    String[] randWords = new String[len];
    Random rand = new Random();
    for (int i = 0; i < len; i++) {
      randWords[i] = wordList.get(rand.nextInt(wordList.size())).toLowerCase();
    }
    return randWords;
  }

  String join(ArrayList<String> arr) {
    String output = "";
    for (String str : arr) {
      output += str + "\n";
    }
    return output;
  }

  ArrayList<String> readFromInputStream(InputStream inputStream) throws IOException {
    ArrayList<String> words = new ArrayList<String>();
    try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
      String line;
      while ((line = br.readLine()) != null) {
        words.add(line);
      }
    }
    return words;
  }
}

class Utils {
  @SuppressWarnings("unused")
  int arrayLength(String[] args) {
    int length = 0;
    for (String arg : args) {
      length += 1;
    }
    return length;
  }

  String stringify(String[] array) {
    String output = "";
    for (String str : array) {
      output += str;
    }
    return output;
  }

  boolean stringArrayContains(String str, String[] args) {
    boolean doesContain = false;
    for (String arg : args) {
      if (str.equals(arg)) {
        doesContain = true;
      }
    }
    return doesContain;
  }
}