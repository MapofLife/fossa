(ns fossa.core-test
  (:use cascalog.api
        [midje sweet cascalog]
        fossa.core))

(fact
  "Test read-occurrences using sample dataset"
  (read-occurrences)
  => (produces-some [["Passer domesticus" "999999999" "-40.8747" "170.851" "" "2007" "6" "4"]
                     ["Passer domesticus" "111111111" "-40.8747" "170.851" "10" "2007" "" ""]
                     ["Passer domesticus" "333333333" "-40.8747283" "170.851" "" "2007" "" ""]
                     ["Passer domesticus" "444444444" "-40.8747283" "170.851" "" "2007" "" ""]
                     ["Passer domesticus" "222222222" "-40.8747283" "170.851" "10" "2007" "" ""]]))

(fact
  "Test collect-update-stmts"
  (let [partition-num 2
        tuples [["0" "0" "10" "Acidobacteria" "100000000" "" "5" "2008"]
                ["0" "0" "3" "Acidobacteria" "500000000" "" "7" "2010"]]]
    (collect-update-stmts tuples partition-num))
  => [[2 "UPDATE gbif_points SET the_geom_multipoint = ST_GeomFromWKB(ST_AsBinary('000000000400000001000000000100000000000000000000000000000000'::geometry), 4326) WHERE name = 'Acidobacteria' AND partition = 2;"]
      [2 "UPDATE gbif_points SET months = '{\"10,3\"}' WHERE name = 'Acidobacteria' AND partition = 2;"]
      [2 "UPDATE gbif_points SET occids = '{\"100000000,500000000\"}' WHERE name = 'Acidobacteria' AND partition = 2;"]
      [2 "UPDATE gbif_points SET precisions = '{\",\"}' WHERE name = 'Acidobacteria' AND partition = 2;"]
      [2 "UPDATE gbif_points SET season = '{\"5,7\"}' WHERE name = 'Acidobacteria' AND partition = 2;"]
      [2 "UPDATE gbif_points SET years = '{\"2008,2010\"}' WHERE name = 'Acidobacteria' AND partition = 2;"]])

(fact
  "Test parse-occurrence-data with 10000 element partition"
  (let [src (parse-occurrence-data (read-occurrences))]
    (<- [?partition ?stmt]
        (src _ ?partition ?stmt)))
  => (produces-some
      [[0 "UPDATE gbif_points SET the_geom_multipoint = ST_GeomFromWKB(ST_AsBinary('0000000004000000030000000001000000000000000000000000000000000000000001C051A88CE703AFB84044C9C200C0F0200000000001401893E63E8DDA49404ABEC12AD81ADF'::geometry), 4326) WHERE name = 'Acidobacteria' AND partition = 0;"]
       [0 "UPDATE gbif_points SET months = '{\"10,3\", \"6\", \"5\"}' WHERE name = 'Acidobacteria' AND partition = 0;"]
       [0 "UPDATE gbif_points SET occids = '{\"100000000,500000000\", \"242135541\", \"244661001\"}' WHERE name = 'Acidobacteria' AND partition = 0;"]
       [0 "UPDATE gbif_points SET precisions = '{\",\", \"\", \"\"}' WHERE name = 'Acidobacteria' AND partition = 0;"]
       [0 "UPDATE gbif_points SET season = '{\"5,7\", \"2\", \"2\"}' WHERE name = 'Acidobacteria' AND partition = 0;"]
       [0 "UPDATE gbif_points SET years = '{\"2008,2010\", \"2007\", \"2008\"}' WHERE name = 'Acidobacteria' AND partition = 0;"]
       [0 "UPDATE gbif_points SET the_geom_multipoint = ST_GeomFromWKB(ST_AsBinary('000000000400000002000000000140655B3B645A1CACC0446FF62B6AE7D5000000000140655B3B645A1CACC0446FF718D0B15E'::geometry), 4326) WHERE name = 'Passer domesticus' AND partition = 0;"]
       [0 "UPDATE gbif_points SET months = '{\",6\", \",,\"}' WHERE name = 'Passer domesticus' AND partition = 0;"]
       [0 "UPDATE gbif_points SET occids = '{\"111111111,999999999\", \"333333333,444444444,222222222\"}' WHERE name = 'Passer domesticus' AND partition = 0;"]
       [0 "UPDATE gbif_points SET precisions = '{\"10,\", \",,10\"}' WHERE name = 'Passer domesticus' AND partition = 0;"]
       [0 "UPDATE gbif_points SET season = '{\",4\", \",,\"}' WHERE name = 'Passer domesticus' AND partition = 0;"]
       [0 "UPDATE gbif_points SET years = '{\"2007,2007\", \"2007,2007,2007\"}' WHERE name = 'Passer domesticus' AND partition = 0;"]]))

(fact
  "Test parse-occurrence-data with 2 element partition"
  (let [src (parse-occurrence-data (read-occurrences) :partition-size 2)]
    (<- [?partition ?stmt]
        (src _ ?partition ?stmt)))
  => (produces-some
      [[0 "UPDATE gbif_points SET the_geom_multipoint = ST_GeomFromWKB(ST_AsBinary('000000000400000001000000000100000000000000000000000000000000'::geometry), 4326) WHERE name = 'Acidobacteria' AND partition = 0;"]
       [0 "UPDATE gbif_points SET months = '{\"10,3\"}' WHERE name = 'Acidobacteria' AND partition = 0;"]
       [0 "UPDATE gbif_points SET occids = '{\"100000000,500000000\"}' WHERE name = 'Acidobacteria' AND partition = 0;"]
       [0 "UPDATE gbif_points SET precisions = '{\",\"}' WHERE name = 'Acidobacteria' AND partition = 0;"]
       [0 "UPDATE gbif_points SET season = '{\"5,7\"}' WHERE name = 'Acidobacteria' AND partition = 0;"]
       [0 "UPDATE gbif_points SET years = '{\"2008,2010\"}' WHERE name = 'Acidobacteria' AND partition = 0;"]
       [1 "UPDATE gbif_points SET the_geom_multipoint = ST_GeomFromWKB(ST_AsBinary('0000000004000000020000000001C051A88CE703AFB84044C9C200C0F0200000000001401893E63E8DDA49404ABEC12AD81ADF'::geometry), 4326) WHERE name = 'Acidobacteria' AND partition = 1;"]
       [1 "UPDATE gbif_points SET months = '{\"6\", \"5\"}' WHERE name = 'Acidobacteria' AND partition = 1;"]
       [1 "UPDATE gbif_points SET occids = '{\"242135541\", \"244661001\"}' WHERE name = 'Acidobacteria' AND partition = 1;"]
       [1 "UPDATE gbif_points SET precisions = '{\"\", \"\"}' WHERE name = 'Acidobacteria' AND partition = 1;"]
       [1 "UPDATE gbif_points SET season = '{\"2\", \"2\"}' WHERE name = 'Acidobacteria' AND partition = 1;"]
       [1 "UPDATE gbif_points SET years = '{\"2007\", \"2008\"}' WHERE name = 'Acidobacteria' AND partition = 1;"]]))

(fact
  "Test parse-occurrence-data with large number of observations"
  (let [src (into [["Passer domesticus" "999999999" "-40.8747" "170.851" "" "2007" "6" "4"]]
                  (repeat MAX-OBS ["Really big ants" "222222222" "-40.8747283" "170.851" "10" "2007" "" ""]))
        partitioned-src (parse-occurrence-data src)]
    (<- [?name]
        (partitioned-src ?name ?partition ?stmt)
        (= ?name "Really big ants")))
  => (produces []))
