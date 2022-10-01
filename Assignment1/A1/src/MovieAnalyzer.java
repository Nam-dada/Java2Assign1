import java.util.*;
import java.io.*;
import java.util.stream.Collectors;


public class MovieAnalyzer {

    public class Movie {
        private final String Series_Title;
        private final String Certificate;
        private final String Runtime;
        private final String Genre;
        private final String Overview;
        private final String Director;
        private final String Star1;
        private final String Star2;
        private final String Star3;
        private final String Star4;

        private final int Released_Year;
        private final String Meta_score;
        private final String Noofvotes;
        private final String Gross;
        private final double IMDB_Rating;

        public Movie(String series_Title, String released_Year, String certificate, String runtime,
                     String genre, String IMDB_Rating, String overview, String meta_score, String director,
                     String star1, String star2, String star3, String star4, String noofvotes, String gross
        ) {
            Series_Title = series_Title;
            Certificate = certificate;
            Runtime = runtime;
            Genre = genre;
            Overview = overview;
            Director = director;
            Star1 = star1;
            Star2 = star2;
            Star3 = star3;
            Star4 = star4;
            Released_Year = Integer.parseInt(released_Year);
            Meta_score = meta_score;
            Noofvotes = noofvotes;
            Gross = gross;
            this.IMDB_Rating = Double.parseDouble(IMDB_Rating);
        }

        public String getSeries_Title() {
            return Series_Title;
        }

        public String getCertificate() {
            return Certificate;
        }

        public String getRuntime() {
            return Runtime;
        }

        public String getGenre() {
            return Genre;
        }

        public String getOverview() {
            return Overview;
        }

        public String getDirector() {
            return Director;
        }

        public String getStar1() {
            return Star1;
        }

        public String getStar2() {
            return Star2;
        }

        public String getStar3() {
            return Star3;
        }

        public String getStar4() {
            return Star4;
        }

        public int getReleased_Year() {
            return Released_Year;
        }

        public String getMeta_score() {
            return Meta_score;
        }

        public String getNoofvotes() {
            return Noofvotes;
        }

        public String getGross() {
            return Gross;
        }

        public double getIMDB_Rating() {
            return IMDB_Rating;
        }
    }

    static List<Movie> movies = new ArrayList<>();

    public MovieAnalyzer(String dataset_path) {

        try (BufferedReader infile
                     = new BufferedReader(new FileReader(dataset_path))) {
            String line = infile.readLine();

//            while ((line = infile.readLine()) != null) {
//                String[] a = dealString(line);
//                movies.add(new Movie(a[1], a[2], a[3], a[4], a[5],
//                        a[6], a[7], a[8], a[9], a[10],
//                        a[11], a[12], a[13], a[14], a[15]));
//            }
            movies = infile.lines()
                    .map(this::dealString)
                    .map(a -> new Movie(a[1], a[2], a[3], a[4], a[5],
                            a[6], a[7], a[8], a[9], a[10],
                            a[11], a[12], a[13], a[14], a[15]))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String[] dealString(String info) {
        String[] repo = new String[16];
        char[] temp = info.concat(",").toCharArray();
        int id = 0, l = 0;
        boolean flag = false;
        int cnt = 0;
        for (int i = 0; i < temp.length; i++) {
            if (temp[i] == '\"') {
                cnt++;
                if ((i + 1) < temp.length && temp[i + 1] == '\"') continue;
                if ((i - 1) > -1 && temp[i - 1] == '\"') {
                    cnt -= 2;
                    if (cnt % 2 == 1) continue;
                }

                if (flag) {
                    repo[id] = info.substring(l, i);
                    ++id;
                    ++i;
                }
                flag = !flag;
                l = i + 1;
                continue;
            }
            if (temp[i] == ',' && !flag) {
                repo[id] = info.substring(l, i);
                ++id;
                l = i + 1;
            }
        }
        return repo;
    }

    public static Map<Integer, Integer> getMovieCountByYear() {
//        Map<Integer, Integer> temp = movies.stream().collect(Collectors.groupingBy(
//                Movie::getReleased_Year,
//                () -> new TreeMap<>(Comparator.reverseOrder()),
//                Collectors.reducing(0, movie->1, Integer::sum)
//        ));
        return movies.stream().collect(Collectors.groupingBy(
                Movie::getReleased_Year,
                TreeMap::new,
                Collectors.reducing(0, movie -> 1, Integer::sum))).descendingMap();
    }

    public static Map<String, Integer> getMovieCountByGenre() {
        Map<String, Integer> temp = movies.stream().collect(Collectors.groupingBy(
                Movie::getGenre,
                Collectors.reducing(0, movie -> 1, Integer::sum)
        ));

        List<Map.Entry<String, Integer>> t1 = new ArrayList<>(temp.entrySet());
        Collections.sort(t1, (Map.Entry<String, Integer> a, Map.Entry<String, Integer> b) -> {
            if (!a.getValue().equals(b.getValue())) return b.getValue() - a.getValue();
            else return a.getKey().compareTo(b.getKey());
        });

        Map<String, Integer> ftp = new LinkedHashMap<>();
        t1.stream().forEachOrdered(element -> ftp.put(element.getKey(), element.getValue()));

        return ftp;
    }

    public static Map<List<String>, Integer> getCoStarCount() {
        return movies.stream().collect(Collectors.groupingBy(
                movie -> new ArrayList<>(Arrays.asList(movie.getStar1(), movie.getStar2())).stream().sorted().collect(Collectors.toList()),
                Collectors.reducing(0, movie -> 1, Integer::sum)
        ));
    }

    public static void main(String[] args) {
        MovieAnalyzer movieAnalyzer = new MovieAnalyzer("resources/imdb_top_500.csv");
        System.out.println(getCoStarCount());
    }

}