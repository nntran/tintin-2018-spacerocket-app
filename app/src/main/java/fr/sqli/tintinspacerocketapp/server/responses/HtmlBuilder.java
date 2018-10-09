package fr.sqli.tintinspacerocketapp.server.responses;

public class HtmlBuilder {


    private StringBuilder header, body, style;

    private String title;

    private HtmlBuilder() {}


    public static HtmlBuilder html() {
        return new HtmlBuilder();
    }

    public HtmlBuilder title(String title) {
        this.title = title;
        return this;
    }

    public String build() {
        StringBuilder html = new StringBuilder();
        html.append("<html>");

        if (header == null) {
            head(""); // generate empty header
        }
        html.append(header);

        if (body != null)
            html.append(body);

        html.append("</html>");

        return html.toString();
    }

    public HtmlBuilder style(String html) {
        this.style = new StringBuilder("<style>");
        if (html != null)
            this.style.append(html);

        this.style.append("</style>");
        return this;
    }

    public HtmlBuilder defaultTableStyle() {
        StringBuilder tableStyle = new StringBuilder();
        tableStyle.append("table {")
                .append("background:#819FF7;")
                .append("border-collapse:collapse;")
                .append("width:100%;}")
                .append("th { color:white; }")
                .append("th, td {")
                .append("text-align:center;")
                .append("padding:5px;")
                .append("border-right:1px solid black;}")
                .append("th:last-child, td:last-child { border-right:0; }")
                .append("tbody tr:nth-child(odd) { background:#81BEF7; }")
                .append("tbody tr:nth-child(even) { background:#81DAF5; }")
                .append("@media screen and (max-width:800px) {")
                .append("table { display:flex; }")
                .append("thead { width:20%; min-width:90px; }")
                .append("tbody { flex:1; }")
                .append("tr { display:flex; flex-direction:column; }")
                .append("th, td { text-align:left; border-right:0; border-bottom:1px solid black; }")
                .append("tr:last-child td:last-child { border-bottom:0; }")
                .append("tbody tr:not(:first-child) td::before { position:absolute; content:'Pseudo';")
                .append("color:white; font-weight:bold; width:calc(20% - 13px); min-width:90px;")
                .append("padding:5px; border-bottom:1px solid black; margin-left:calc(-20% - 2px); margin-top:-5px; }")
                .append("tbody tr:last-child td:last-child::before { border-bottom:0; }")
                .append("tbody tr:not(:first-child) td:nth-of-type(2)::before { content:'Age'; }")
                .append("tbody tr:not(:first-child) td:nth-of-type(3)::before { content:'Classement'; }")
                .append("} @media screen and (max-width:461px) { tbody tr:not(:first-child) td::before { margin-left:-95px; } }");

        return style(tableStyle.toString());
    }

    public HtmlBuilder head(String html) {
        this.header = new StringBuilder("<head>");
        this.header.append("<meta charset=\"utf-8\"/>");
        if (title != null)
            this.header.append("<title>" + title + "</title>");

        if (this.style != null)
            this.header.append(this.style);

        if (html != null)
            this.header.append(html);

        this.header.append("</head>");
        return this;
    }

    public HtmlBuilder body(String html) {
        this.body = new StringBuilder("<body>");
        if (html != null)
            this.body.append(html);

        this.body.append("</body>");
        return this;
    }
}
