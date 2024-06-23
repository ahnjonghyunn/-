    import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class 안종현_20201906_JAVA프로젝트 extends JFrame implements ActionListener {
    private JComboBox<String> movieComboBox;
    private JButton bookButton;
    private JButton cancelButton;
    private JButton viewButton;
    private JButton movieInfoButton;
    private JTextArea bookingStatusTextArea;

    private ArrayList<Movie> movies;
    private Map<String, Reservation> reservations;

    private JButton[][] seatButtons;

    public 안종현_20201906_JAVA프로젝트() {
        setTitle("영화 예매 시스템");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 600);
        setLayout(new FlowLayout());

        movieComboBox = new JComboBox<>();
        add(movieComboBox);

        movieInfoButton = new JButton("영화 정보");
        movieInfoButton.addActionListener(this);
        add(movieInfoButton);

      
        bookButton = new JButton("예매");
        bookButton.addActionListener(this);
        add(bookButton);

        cancelButton = new JButton("취소");
        cancelButton.addActionListener(this);
        add(cancelButton);

        viewButton = new JButton("예매 조회");
        viewButton.addActionListener(this);
        add(viewButton);

        bookingStatusTextArea = new JTextArea(10, 30);
        add(bookingStatusTextArea);
        
        seatButtons = new JButton[5][5];

        JPanel seatPanel = new JPanel(new GridLayout(5, 5));
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                JButton button = new JButton("Seat " + (i * 5 + j + 1));
                button.addActionListener(this);
                seatButtons[i][j] = button;
                seatPanel.add(button);
            }
        }
        add(seatPanel);

        addMovieData();

        reservations = new HashMap<>();

        setVisible(true);
    }

    private void addMovieData() {
        movies = new ArrayList<>();
        movies.add(new Movie("범죄도시3", "이상용", "액션,범죄,스릴러", "1시간 45분"));
        movies.add(new Movie("트랜드포머(비스트의 사막)", "스티븐 케이플 주니어", "sf,모험,판타지", "2시간 7분"));
        movies.add(new Movie("존윅4", "채드 스타헬스키", "범죄,액션,스릴러", "2시간 49분"));

        for (Movie movie : movies) {
            movieComboBox.addItem(movie.getTitle());
        }
    }

    private void bookMovie() { //선택된 좌석을 예매할 때 버튼을 비활성화하고, 예매 완료 후에 예매 상태를 표시하는 부분
        String selectedMovieTitle = (String) movieComboBox.getSelectedItem();
        if (selectedMovieTitle != null) {
            Movie selectedMovie = getMovieByTitle(selectedMovieTitle);

            boolean seatSelected = false;
            StringBuilder selectedSeats = new StringBuilder();
            for (JButton[] row : seatButtons) {
                for (JButton button : row) {
                    if (button.isEnabled() && button.isSelected()) {
                        if (selectedSeats.length() > 0) {
                            selectedSeats.append(", ");
                        }
                        selectedSeats.append(button.getText());
                        seatSelected = true;
                        button.setEnabled(false);
                    }
                }
            }

            if (seatSelected) {
            	
                Reservation reservation = new Reservation(selectedMovie, selectedSeats.toString());
                reservations.put(reservation.getReservationNumber(), reservation);

                String bookingStatus = "예매 완료\n예매 번호: " + reservation.getReservationNumber() +
                        "\n영화: " + selectedMovie.getTitle() +
                        "\n좌석: " + reservation.getSeat();
                bookingStatusTextArea.setText(bookingStatus);
            } else {
                bookingStatusTextArea.setText("좌석을 선택해주세요.");
            }
        }
    }

    private void cancelReservation() {// 예매를 취소할 때 선택된 좌석의 버튼을 다시 활성화하고, 예매 취소 상태를 표시하는 부분
        String reservationNumber = JOptionPane.showInputDialog("예매 번호를 입력하세요:");
        Reservation reservation = reservations.get(reservationNumber);
        if (reservation != null) {
            reservations.remove(reservationNumber);

            
            String[] selectedSeats = reservation.getSeat().split(", ");
            for (String seat : selectedSeats) {
                for (JButton[] row : seatButtons) {
                    for (JButton button : row) {
                        if (button.getText().equals(seat)) {
                            button.setEnabled(true);
                        }
                    }
                }
            }

            bookingStatusTextArea.setText("예매가 취소되었습니다.");
        } else {
            bookingStatusTextArea.setText("예매 번호를 확인하세요.");
        }
    }

    private void viewReservations() {
        String[] options = {"전체 예매 내역", "예매 번호 검색"};
        int selectedOption = JOptionPane.showOptionDialog(this, "예매 조회 방법을 선택하세요:", "예매 조회", JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

        switch (selectedOption) {
            case 0:
            	showAllReservations();
                break;
            case 1:
                showReservationByNumber();
                break;
        }
    }

    private void showAllReservations() {
        if (!reservations.isEmpty()) {
            StringBuilder bookingStatus = new StringBuilder("전체 예매 내역:\n");
            for (Reservation reservation : reservations.values()) {
                bookingStatus.append("\n예매 번호: ").append(reservation.getReservationNumber())
                        .append("\n영화: ").append(reservation.getMovie().getTitle())
                        .append("\n좌석: ").append(reservation.getSeat()).append("\n");
            }
            bookingStatusTextArea.setText(bookingStatus.toString());
        } else {
            bookingStatusTextArea.setText("예매된 내역이 없습니다.");
        }
    }
    


    private void showReservationByNumber() {
        String reservationNumber = JOptionPane.showInputDialog("예매 번호를 입력하세요:");
        Reservation reservation = reservations.get(reservationNumber);
        if (reservation != null) {
            String bookingStatus = "예매 번호: " + reservation.getReservationNumber() +
                    "\n영화: " + reservation.getMovie().getTitle() +
                    "\n좌석: " + reservation.getSeat();
            bookingStatusTextArea.setText(bookingStatus);
        } else {
            bookingStatusTextArea.setText("예매 번호를 확인하세요.");
        }
    }

    private void showMovieInfo() {
        String selectedMovieTitle = (String) movieComboBox.getSelectedItem();
        if (selectedMovieTitle != null) {
            Movie selectedMovie = getMovieByTitle(selectedMovieTitle);
            String movieInfo = "영화 제목: " + selectedMovie.getTitle() +
                    "\n감독: " + selectedMovie.getDirector() +
                    "\n장르: " + selectedMovie.getGenre() +
                    "\n시간: " + selectedMovie.getRunningTime();
            bookingStatusTextArea.setText(movieInfo);
        }
    }

    private Movie getMovieByTitle(String title) {
        for (Movie movie : movies) {
            if (movie.getTitle().equals(title)) {
                return movie;
            }
        }
        return null;
    }

   
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == bookButton) {
            bookMovie();
        } else if (e.getSource() == cancelButton) {
            cancelReservation();
        } else if (e.getSource() == viewButton) {
            viewReservations();
        } else if (e.getSource() == movieInfoButton) {
            showMovieInfo();
        } else {
            for (JButton[] row : seatButtons) {
                for (JButton button : row) {
                    if (button == e.getSource()) {
                        button.setSelected(!button.isSelected());
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        new 안종현_20201906_JAVA프로젝트();
    }
}


class Movie {
    private String title;
    private String director;
    private String genre;
    private String runningTime;

    public Movie(String title, String director, String genre, String runningTime) {
        this.title = title;
        this.director = director;
        this.genre = genre;
        this.runningTime = runningTime;
    }

    public String getTitle() {
        return title;
    }

    public String getDirector() {
        return director;
    }

    public String getGenre() {
        return genre;
    }

    public String getRunningTime() {
        return runningTime;
    }
}

class Reservation {
    private static int reservationCounter = 0;

    private String reservationNumber;
    private Movie movie;
    private String seat;

    public Reservation(Movie movie, String seat) {
        reservationCounter++;
        this.reservationNumber = "R" + reservationCounter;
        this.movie = movie;
        this.seat = seat;
    }

    public String getReservationNumber() {
        return reservationNumber;
    }

    public Movie getMovie() {
        return movie;
    }

    public String getSeat() {
        return seat;
    }
}
    
