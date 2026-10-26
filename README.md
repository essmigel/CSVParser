# CSV Viewer

An Android app that downloads [issues.csv](https://raw.githubusercontent.com/RabobankDev/AssignmentCSV/main/issues.csv), converts its values to strings, integers and dates, and shows every row as a card.

To try another file, change `CSV_URL` in `di/AppModule.kt`.

# Solution Notes

## Architecture
The app follows MVI on top of clean architecture, split into `data`, `domain` and `presentation` packages. The CSV parser is written from scratch and lives in its own pure Kotlin module, `:csv-parser`, so it has no Android dependencies. The file is parsed while it is being downloaded.

## CSV rules
- The first record is the header and its values are used as labels.
- Fields are separated by commas. A quoted field can contain commas, line breaks and quotes written twice (`""`).
- Every row must have as many values as the header, otherwise the file is rejected with an error that names the row.
- A column is an integer or a date column only when all of its non-blank values are. One value that does not fit makes the whole column text.
- Integers with a leading zero or a plus sign (`007`, `+31...`) stay text, so identifiers and phone numbers are not changed.
- Dates are read in ISO format (`1978-01-02T00:00:00` or `1978-01-02`). Other formats stay text.
- Blank values are shown as an empty value.

## Side Notes
- Values that link to an image are shown as an image. The avatar URLs in the sample file are blocked by the server (403), so the URL is shown as text instead.