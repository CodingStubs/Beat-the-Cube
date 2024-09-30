import java.util.*;

public class Main {
    public static void main(String[] args) {
        for (int i = 0; i < 2; i++) {
            simulateGame();
        }
    }

    public static void simulateGame() {
        Scanner sc = new Scanner(System.in);
        double winCount = 0;

        System.out.println("What simulation do you want?");
        System.out.println("[0] Utilize Card Counts, [1] Naive Approach (above/below 7)");
        int mode = sc.nextInt();

        System.out.println("How many games do you want to simulate?");
        int totalCount = sc.nextInt();

        for (int i = 0; i < totalCount; i++) {
            boolean win = runSimulation(mode);
            if (win) winCount++;
        }
        System.out.println("Win Rate: " + winCount/totalCount);
    }

    public static boolean runSimulation(int mode) {
        int[] counts = initializeCounts();
        List<Card> deck = shuffleCards();

        Card[][] gameBoard = getInitialGameBoard(deck, counts);

        boolean success = true; //false = game Over : true = cont
        while (success && !deck.isEmpty()) {
            if (mode == 0) {
                success = makeCountMove(counts, deck, gameBoard);
            } else if (mode == 1) {
                success = makeNaiveMove(counts, deck, gameBoard);
            }
        }

        //System.out.println(deck.size());

        if (!success) {
            //System.out.println("You Lost");
            return false;
        } else {
            //System.out.println("You Win");
            return true;
        }
    }

    public static boolean makeNaiveMove(int[] counts, List<Card> deck, Card[][] gameBoard) {
        int maxABS = -1;
        int maxI = -1;
        int maxJ = -1;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (gameBoard[i][j] != null) {
                    Card c = gameBoard[i][j];
                    int cardRank = Math.abs(c.countsIdx - 6);
                    if (cardRank > maxABS) {
                        maxABS = cardRank;
                        maxI = i;
                        maxJ = j;
                    }
                }
            }
        }
        if (maxI == -1) //We lost, everything covered up
            return false;

        char guess = 'H';
        Card guessSpot = gameBoard[maxI][maxJ];
        int oldIndex = guessSpot.countsIdx;
        if (oldIndex > 6) { //card indexes are 0-indexes, so > 6 means a card greater than 7
            guess = 'L';
        }

        Card newCard = deck.remove(0);
        int newIndex = newCard.countsIdx;

        if (checkGuess(oldIndex, newIndex, guess)) {
            gameBoard[maxI][maxJ] = newCard;
        } else {
            gameBoard[maxI][maxJ] = null;
        }
        return true;
    }

    public static boolean checkGuess(int oldIndex, int newIndex, char guess) {
        if (guess == 'H') {
            if (newIndex > oldIndex) {
                return true; //Success!
            } else {
                return false; //Fail!
            }
        } else if (guess == 'L') {
            if (newIndex < oldIndex) {
                return true; //Success!
            } else {
                return false; //Fail!
            }
        }
        return true;
    }

    public static boolean makeCountMove(int[] counts, List<Card> deck, Card[][] gameBoard) {
        int maxABS = -1;
        int maxI = -1;
        int maxJ = -1;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (gameBoard[i][j] != null) {
                    Card c = gameBoard[i][j];
                    int cardCount = counts[c.countsIdx];
                    if (Math.abs(cardCount) > maxABS) {
                        maxABS = Math.abs(cardCount);
                        maxI = i;
                        maxJ = j;
                    }
                }
            }
        }
        if (maxI == -1) //We lost, everything covered up
            return false;

        char guess = 'H';
        Card guessSpot = gameBoard[maxI][maxJ];
        int cardCount = counts[guessSpot.countsIdx];
        int oldIndex = guessSpot.countsIdx;
        if (cardCount < 0) {
            guess = 'L';
        }

        Card newCard = deck.remove(0);
        int newIndex = newCard.countsIdx;

        if (checkGuess(oldIndex, newIndex, guess)) {
            gameBoard[maxI][maxJ] = newCard;
        } else {
            gameBoard[maxI][maxJ] = null;
        }

        //Update counts HERE
        for (int i = 0; i < counts.length; i++) {
            if (i < newIndex) {
                counts[i]--;
            } else if (i > newIndex) {
                counts[i]++;
            }
        }

        return true;
    }

    public static Card[][] getInitialGameBoard(List<Card> deck, int[] counts) {
        Card[][] gameBoard = new Card[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Card newCard = deck.remove(0);
                int newIndex = newCard.countsIdx;
                gameBoard[i][j] = newCard;

                //Update counts HERE
                for (int k = 0; k < counts.length; k++) {
                    if (k < newIndex) {
                        counts[k]--;
                    } else if (k > newIndex) {
                        counts[k]++;
                    }
                }
            }
        }
        return gameBoard;
    }

    public static int[] initializeCounts() {
        int[] counts = new int[13]; //Counts is +(amt of cards above this rank) or -(vice versa)
        for (int i = 0; i < counts.length; i++) {
            counts[i] = 48 - (i*8);
        }
        return counts;
    }

    public static List<Card> shuffleCards() {
        List<Card> deck = new ArrayList<>();
        for (int i = 0; i < 52; i++) {
            deck.add(new Card(i+4));
        }
        Collections.shuffle(deck);
        return deck;
    }

    private static class Card {

        public String suite;
        public String rank;
        public int countsIdx;

        public Card(int i) {
            int idx = i/4;
            this.countsIdx = idx - 1;
            if (idx == 1) { //Ace
                this.rank = "A";
            } else if (idx == 11) { //J
                this.rank = "J";
            } else if (idx == 12) { //Q
                this.rank = "Q";
            } else if (idx == 13) { //K
                this.rank = "K";
            } else {
                this.rank = Integer.toString(idx);
            }

            int s = i % 4;
            if (s == 0) {
                this.suite = "Hearts";
            } else if (s == 1) {
                this.suite = "Clubs";
            } else if (s == 2) {
                this.suite = "Diamonds";
            } else if (s == 3) {
                this.suite = "Spades";
            }
        }

        public String toString() {
            return this.rank + ": " + this.suite;
        }
    }
}