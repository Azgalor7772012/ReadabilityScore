package readability;

import java.io.*;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Main {

    static String[] ages = {"6", "7", "9", "10", "11", "12", "13", "14", "15",
            "16", "17", "18", "24", "24+"};
    static double yearsRI = 0;
    static double yearsFK = 0;
    static double yearsSmog = 0;
    static double yearsColeman = 0;

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        File text = new File(args[0]);

        //Все чарактеры
        char[] AllCharacters = new char[(int) text.length()];


        try (FileReader read = new FileReader(text)) {
            read.read(AllCharacters);
        } catch (IOException e) {
            e.printStackTrace();
        }


        //Текст вместе
        String allText = String.valueOf(AllCharacters);

        // Все чарактеры без пробелов
        String characters = allText.replaceAll("\\s+", ""); //Меняем все пробелы на ничего
        int amountCharacters = characters.length();

        //Слова
        int amountWords = allText.split(" ").length;
        //Предложения
        int amountSentences = allText.split("[!?.]").length;
        //Слоги
        int amountSyllables = totalSyllables(allText);
        //Сложные слова
        int amountPolysyllables = totalPolysyllables(allText);

        //Печатаем слова, предложения и символы
        System.out.printf("Words: %d\n", amountWords);
        System.out.printf("Sentences: %d\n", amountSentences);
        System.out.printf("Characters: %d\n", amountCharacters);
        System.out.printf("Syllables: %d\n", amountSyllables);
        System.out.printf("Polysyllables: %d\n", amountPolysyllables);

        //Просим ввести каким способом необходимо посчитать трудность чтения текста
        System.out.println("Enter the score you want to calculate (ARI, FK, SMOG, CL, all): ");
        String choice = scanner.next();

        switch(choice) {
            case "ARI":
                readabilityIndex(amountCharacters,amountWords,amountSentences);
                break;
            case "FK":
                fleschKinc(amountWords, amountSyllables, amountSentences);
                break;
            case "SMOG":
                smog(amountPolysyllables,amountSentences);
                break;
            case "CL":
                coleman(amountCharacters, amountWords, amountSentences);
                break;
            case "all":
                readabilityIndex(amountCharacters,amountWords,amountSentences);
                fleschKinc(amountWords, amountSyllables, amountSentences);
                smog(amountPolysyllables,amountSentences);
                coleman(amountCharacters, amountWords, amountSentences);
                averageYears();
                break;

        }
    }
    //Находим сколько слогов в слове
    static int findSyllables (String word) {
        StringBuilder workWord = new StringBuilder();
        if (word.charAt(word.length() - 1) == 'e') {
            workWord.append(word, 0, word.length() - 1).append('t');
        } else {
            workWord.append(word);
        }

        Pattern pattern = Pattern.compile("[aeiouy]+");
        Matcher matcher = pattern.matcher(workWord);

        int cnt = 0;
        while(matcher.find()) {
            cnt++;
        }

        return cnt == 0 ? 1 : cnt;

    }
    // Всего слогов во всем тексте
    static int totalSyllables(String text) {
        String[] words = text.split(" ");
        int totalSyll = 0;
        for (String elem : words) {
            totalSyll += findSyllables(elem);
        }
        return totalSyll;
    }
    // Всего сложных слов(больше 2 слогов) во всем тексте
    static int totalPolysyllables(String text) {
        String[] words = text.split(" ");
        int totalPoly = 0;
        for (String elem : words) {
            if(findSyllables(elem) > 2) {
                totalPoly++;
            }
        }
        return totalPoly;
    }
    // Рассчет всех индексов и среднего количество лет для чтения, если выбраны все индексы

    static void readabilityIndex(int characters, int words, int sentences) {
        double score = (4.71 * ((double) characters / words) + 0.5 * ((double) words / sentences) - 21.43);
        System.out.printf("Automated Readability Index: %.2f (about %s-year-olds).\n", score, ages[(int)Math.round(score - 1)]);
        if (isValid(score)) {
            yearsRI = Double.parseDouble(ages[(int)Math.round(score - 1)]);
        }
    }

    static void fleschKinc(int words, int syll, int sentences) {
        double score = 0.39 * ((double) words / sentences) + 11.8 * ((double) syll / words) -15.59;
        System.out.printf("Flesch–Kincaid readability tests: %.2f (about %s-year-olds).\n", score, ages[(int)Math.round(score - 1)]);
        if (isValid(score)) {
            yearsFK = Double.parseDouble(ages[(int)Math.round(score - 1)]);
        }
    }

    static void smog (int poly, int sentences) {
        double score = 1.043 * Math.sqrt(poly * ((double) 30 / sentences)) + 3.1291;
        System.out.printf("Simple Measure of Gobbledygook: %.2f (about %s-year-olds).\n",score, ages[(int)Math.round(score - 1)]);
        if (isValid(score)) {
            yearsSmog = Double.parseDouble(ages[(int)Math.round(score - 1)]);
        }
    }

    static void coleman(int characters, int words, int sentences) {
        double l = ((double) characters / words) * 100;
        double s = ((double) sentences / words) * 100;
        double score = 0.0588 * l - 0.296 * s - 15.8;
        System.out.println(score);
        System.out.printf("Coleman–Liau index: %.2f (about %s-year-olds).\n",score, ages[(int)Math.round(score - 1)]);
        if (isValid(score)) {
            yearsColeman = Double.parseDouble(ages[(int)Math.round(score - 1)]);
        }
    }

    static void averageYears() {
        if (yearsRI > 0 && yearsColeman > 0 && yearsSmog > 0 && yearsFK > 0) {
            double averageIndex = (yearsRI + yearsFK + yearsSmog + yearsColeman) / 4;
            System.out.printf("This text should be understood in average by %.2f-year-olds.", averageIndex);
        } else {
            System.out.println("Can't calculate average years");
        }

    }

    static boolean isValid(double score) {
        return score < 13;
    }

}
