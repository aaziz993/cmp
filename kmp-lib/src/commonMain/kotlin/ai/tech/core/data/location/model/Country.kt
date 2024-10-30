package ai.tech.core.data.location.model

public data class Country(
    public val officialName: String,
    public val alpha2: String,
    public val alpha3: String,
    public val numeric: String,
    public val phoneCode: String,
)

public val countries: Map<String, () -> Country> =
    mapOf(
        "AD" to {
            Country(
                "Andorra",
                "AD",
                "AND",
                "020",
                "+376",
            )
        },
        "AE" to {
            Country(
                "United Arab Emirates (UAE)",
                "AE",
                "ARE",
                "784",
                "+971",
            )
        },
        "AF" to {
            Country(
                "Afghanistan",
                "AF",
                "AFG",
                "004",
                "+93",
            )
        },
        "AG" to {
            Country(
                "Antigua and Barbuda",
                "AG",
                "ATG",
                "028",
                "+1",
            )
        },
        "AI" to {
            Country(
                "Anguilla",
                "AI",
                "AIA",
                "660",
                "+1",
            )
        },
        "AL" to {
            Country(
                "Albania",
                "AL",
                "ALB",
                "008",
                "+355",
            )
        },
        "AM" to {
            Country(
                "Armenia",
                "AM",
                "ARM",
                "051",
                "+374",
            )
        },
        "AO" to {
            Country(
                "Angola",
                "AO",
                "AGO",
                "024",
                "+244",
            )
        },
        "AQ" to {
            Country(
                "Antarctica",
                "AQ",
                "ATA",
                "010",
                "+672",
            )
        },
        "AR" to {
            Country(
                "Argentina",
                "AR",
                "ARG",
                "032",
                "+54",
            )
        },
        "AS" to {
            Country(
                "American Samoa",
                "AS",
                "ASM",
                "016",
                "+1",
            )
        },
        "AT" to {
            Country(
                "Austria",
                "AT",
                "AUT",
                "040",
                "+43",
            )
        },
        "AU" to {
            Country(
                "Australia",
                "AU",
                "AUS",
                "036",
                "+61",
            )
        },
        "AW" to {
            Country(
                "Aruba",
                "AW",
                "ABW",
                "533",
                "+297",
            )
        },
        "AX" to {
            Country(
                "Åland Islands",
                "AX",
                "ALA",
                "248",
                "+358",
            )
        },
        "AZ" to {
            Country(
                "Azerbaijan",
                "AZ",
                "AZE",
                "031",
                "+994",
            )
        },
        "BA" to {
            Country(
                "Bosnia And Herzegovina",
                "BA",
                "BIH",
                "070",
                "+387",
            )
        },
        "BB" to {
            Country(
                "Barbados",
                "BB",
                "BRB",
                "052",
                "+1",
            )
        },
        "BD" to {
            Country(
                "Bangladesh",
                "BD",
                "BGD",
                "050",
                "+880",
            )
        },
        "BE" to {
            Country(
                "Belgium",
                "BE",
                "BEL",
                "056",
                "+32",
            )
        },
        "BF" to {
            Country(
                "Burkina Faso",
                "BF",
                "BFA",
                "854",
                "+226",
            )
        },
        "BG" to {
            Country(
                "Bulgaria",
                "BG",
                "BGR",
                "100",
                "+359",
            )
        },
        "BH" to {
            Country(
                "Bahrain",
                "BH",
                "BHR",
                "048",
                "+973",
            )
        },
        "BI" to {
            Country(
                "Burundi",
                "BI",
                "BDI",
                "108",
                "+257",
            )
        },
        "BJ" to {
            Country(
                "Benin",
                "BJ",
                "BEN",
                "204",
                "+229",
            )
        },
        "BL" to {
            Country(
                "Saint Barthélemy",
                "BL",
                "BLM",
                "652",
                "+590",
            )
        },
        "BM" to {
            Country(
                "Bermuda",
                "BM",
                "BMU",
                "060",
                "+1",
            )
        },
        "BN" to {
            Country(
                "Brunei Darussalam",
                "BN",
                "BRN",
                "096",
                "+673",
            )
        },
        "BO" to {
            Country(
                "Bolivia, Plurinational State Of",
                "BO",
                "BOL",
                "068",
                "+591",
            )
        },
        "BR" to {
            Country(
                "Brazil",
                "BR",
                "BRA",
                "076",
                "+55",
            )
        },
        "BS" to {
            Country(
                "Bahamas",
                "BS",
                "BHS",
                "044",
                "+1",
            )
        },
        "BT" to {
            Country(
                "Bhutan",
                "BT",
                "BTN",
                "064",
                "+975",
            )
        },
        "BW" to {
            Country(
                "Botswana",
                "BW",
                "BWA",
                "072",
                "+267",
            )
        },
        "BY" to {
            Country(
                "Belarus",
                "BY",
                "BLR",
                "112",
                "+375",
            )
        },
        "BZ" to {
            Country(
                "Belize",
                "BZ",
                "BLZ",
                "084",
                "+501",
            )
        },
        "CA" to {
            Country(
                "Canada",
                "CA",
                "CAN",
                "124",
                "+1",
            )
        },
        "CC" to {
            Country(
                "Cocos (keeling) Islands",
                "CC",
                "CCK",
                "166",
                "+61",
            )
        },
        "CD" to {
            Country(
                "Congo, The Democratic Republic Of The",
                "CD",
                "COD",
                "180",
                "+243",
            )
        },
        "CF" to {
            Country(
                "Central African Republic",
                "CF",
                "CAF",
                "140",
                "+236",
            )
        },
        "CG" to {
            Country(
                "Congo",
                "CG",
                "COG",
                "178",
                "+242",
            )
        },
        "CH" to {
            Country(
                "Switzerland",
                "CH",
                "CHE",
                "756",
                "+41",
            )
        },
        "CI" to {
            Country(
                "Côte D'ivoire",
                "CI",
                "CIV",
                "384",
                "+225",
            )
        },
        "CK" to {
            Country(
                "Cook Islands",
                "CK",
                "COK",
                "184",
                "+682",
            )
        },
        "CL" to {
            Country(
                "Chile",
                "CL",
                "CHL",
                "152",
                "+56",
            )
        },
        "CM" to {
            Country(
                "Cameroon",
                "CM",
                "CMR",
                "120",
                "+237",
            )
        },
        "CN" to {
            Country(
                "China",
                "CN",
                "CHN",
                "156",
                "+86",
            )
        },
        "CO" to {
            Country(
                "Colombia",
                "CO",
                "COL",
                "170",
                "+57",
            )
        },
        "CR" to {
            Country(
                "Costa Rica",
                "CR",
                "CRI",
                "188",
                "+506",
            )
        },
        "CU" to {
            Country(
                "Cuba",
                "CU",
                "CUB",
                "192",
                "+53",
            )
        },
        "CV" to {
            Country(
                "Cape Verde",
                "CV",
                "CPV",
                "132",
                "+238",
            )
        },
        "CW" to {
            Country(
                "Curaçao",
                "CW",
                "CUW",
                "531",
                "+599",
            )
        },
        "CX" to {
            Country(
                "Christmas Island",
                "CX",
                "CXR",
                "162",
                "+61",
            )
        },
        "CY" to {
            Country(
                "Cyprus",
                "CY",
                "CYP",
                "196",
                "+357",
            )
        },
        "CZ" to {
            Country(
                "Czech Republic",
                "CZ",
                "CZE",
                "203",
                "+420",
            )
        },
        "DE" to {
            Country(
                "Germany",
                "DE",
                "DEU",
                "276",
                "+49",
            )
        },
        "DJ" to {
            Country(
                "Djibouti",
                "DJ",
                "DJI",
                "262",
                "+253",
            )
        },
        "DK" to {
            Country(
                "Denmark",
                "DK",
                "DNK",
                "208",
                "+45",
            )
        },
        "DM" to {
            Country(
                "Dominica",
                "DM",
                "DMA",
                "212",
                "+1",
            )
        },
        "DO" to {
            Country(
                "Dominican Republic",
                "DO",
                "DOM",
                "214",
                "+1",
            )
        },
        "DZ" to {
            Country(
                "Algeria",
                "DZ",
                "DZA",
                "012",
                "+213",
            )
        },
        "EC" to {
            Country(
                "Ecuador",
                "EC",
                "ECU",
                "218",
                "+593",
            )
        },
        "EE" to {
            Country(
                "Estonia",
                "EE",
                "EST",
                "233",
                "+372",
            )
        },
        "EG" to {
            Country(
                "Egypt",
                "EG",
                "EGY",
                "818",
                "+20",
            )
        },
        "ER" to {
            Country(
                "Eritrea",
                "ER",
                "ERI",
                "232",
                "+291",
            )
        },
        "ES" to {
            Country(
                "Spain",
                "ES",
                "ESP",
                "724",
                "+34",
            )
        },
        "ET" to {
            Country(
                "Ethiopia",
                "ET",
                "ETH",
                "231",
                "+251",
            )
        },
        "FI" to {
            Country(
                "Finland",
                "FI",
                "FIN",
                "246",
                "+358",
            )
        },
        "FJ" to {
            Country(
                "Fiji",
                "FJ",
                "FJI",
                "242",
                "+679",
            )
        },
        "FK" to {
            Country(
                "Falkland Islands (malvinas)",
                "FK",
                "FLK",
                "238",
                "+500",
            )
        },
        "FM" to {
            Country(
                "Micronesia, Federated States Of",
                "FM",
                "FSM",
                "583",
                "+691",
            )
        },
        "FO" to {
            Country(
                "Faroe Islands",
                "FO",
                "FRO",
                "234",
                "+298",
            )
        },
        "FR" to {
            Country(
                "France",
                "FR",
                "FRA",
                "250",
                "+33",
            )
        },
        "GA" to {
            Country(
                "Gabon",
                "GA",
                "GAB",
                "266",
                "+241",
            )
        },
        "GB" to {
            Country(
                "United Kingdom",
                "GB",
                "GBR",
                "826",
                "+44",
            )
        },
        "GD" to {
            Country(
                "Grenada",
                "GD",
                "GRD",
                "308",
                "+1",
            )
        },
        "GE" to {
            Country(
                "Georgia",
                "GE",
                "GEO",
                "268",
                "+995",
            )
        },
        "GF" to {
            Country(
                "French Guyana",
                "GF",
                "GUF",
                "254",
                "+594",
            )
        },
        "GH" to {
            Country(
                "Ghana",
                "GH",
                "GHA",
                "288",
                "+233",
            )
        },
        "GI" to {
            Country(
                "Gibraltar",
                "GI",
                "GIB",
                "292",
                "+350",
            )
        },
        "GL" to {
            Country(
                "Greenland",
                "GL",
                "GRL",
                "304",
                "+299",
            )
        },
        "GM" to {
            Country(
                "Gambia",
                "GM",
                "GMB",
                "270",
                "+220",
            )
        },
        "GN" to {
            Country(
                "Guinea",
                "GN",
                "GIN",
                "324",
                "+224",
            )
        },
        "GP" to {
            Country(
                "Guadeloupe",
                "GP",
                "GLP",
                "312",
                "+450",
            )
        },
        "GQ" to {
            Country(
                "Equatorial Guinea",
                "GQ",
                "GNQ",
                "226",
                "+240",
            )
        },
        "GR" to {
            Country(
                "Greece",
                "GR",
                "GRC",
                "300",
                "+30",
            )
        },
        "GT" to {
            Country(
                "Guatemala",
                "GT",
                "GTM",
                "320",
                "+502",
            )
        },
        "GU" to {
            Country(
                "Guam",
                "GU",
                "GUM",
                "316",
                "+1",
            )
        },
        "GW" to {
            Country(
                "Guinea-bissau",
                "GW",
                "GNB",
                "624",
                "+245",
            )
        },
        "GY" to {
            Country(
                "Guyana",
                "GY",
                "GUY",
                "328",
                "+592",
            )
        },
        "HK" to {
            Country(
                "Hong Kong",
                "HK",
                "HKG",
                "344",
                "+852",
            )
        },
        "HN" to {
            Country(
                "Honduras",
                "HN",
                "HND",
                "340",
                "+504",
            )
        },
        "HR" to {
            Country(
                "Croatia",
                "HR",
                "HRV",
                "191",
                "+385",
            )
        },
        "HT" to {
            Country(
                "Haiti",
                "HT",
                "HTI",
                "332",
                "+509",
            )
        },
        "HU" to {
            Country(
                "Hungary",
                "HU",
                "HUN",
                "348",
                "+36",
            )
        },
        "ID" to {
            Country(
                "Indonesia",
                "ID",
                "IDN",
                "360",
                "+62",
            )
        },
        "IE" to {
            Country(
                "Ireland",
                "IE",
                "IRL",
                "372",
                "+353",
            )
        },
        "IM" to {
            Country(
                "Isle Of Man",
                "IM",
                "IMN",
                "833",
                "+44",
            )
        },
        "IS" to {
            Country(
                "Iceland",
                "IS",
                "ISL",
                "352",
                "+354",
            )
        },
        "IN" to {
            Country(
                "India",
                "IN",
                "IND",
                "356",
                "+91",
            )
        },
        "IO" to {
            Country(
                "British Indian Ocean Territory",
                "IO",
                "IOT",
                "086",
                "+246",
            )
        },
        "IQ" to {
            Country(
                "Iraq",
                "IQ",
                "IRQ",
                "368",
                "+964",
            )
        },
        "IR" to {
            Country(
                "Iran, Islamic Republic Of",
                "IR",
                "IRN",
                "364",
                "+98",
            )
        },
        "IT" to {
            Country(
                "Italy",
                "IT",
                "ITA",
                "380",
                "+39",
            )
        },
        "JE" to {
            Country(
                "Jersey ",
                "JE",
                "JEY",
                "832",
                "+44",
            )
        },
        "JM" to {
            Country(
                "Jamaica",
                "JM",
                "JAM",
                "388",
                "+1",
            )
        },
        "JO" to {
            Country(
                "Jordan",
                "JO",
                "JOR",
                "400",
                "+962",
            )
        },
        "JP" to {
            Country(
                "Japan",
                "JP",
                "JPN",
                "392",
                "+81",
            )
        },
        "KE" to {
            Country(
                "Kenya",
                "KE",
                "KEN",
                "404",
                "+254",
            )
        },
        "KG" to {
            Country(
                "Kyrgyzstan",
                "KG",
                "KGZ",
                "417",
                "+996",
            )
        },
        "KH" to {
            Country(
                "Cambodia",
                "KH",
                "KHM",
                "116",
                "+855",
            )
        },
        "KI" to {
            Country(
                "Kiribati",
                "KI",
                "KIR",
                "296",
                "+686",
            )
        },
        "KM" to {
            Country(
                "Comoros",
                "KM",
                "COM",
                "174",
                "+269",
            )
        },
        "KN" to {
            Country(
                "Saint Kitts and Nevis",
                "KN",
                "KNA",
                "659",
                "+1",
            )
        },
        "KP" to {
            Country(
                "North Korea",
                "KP",
                "PRK",
                "408",
                "+850",
            )
        },
        "KR" to {
            Country(
                "South Korea",
                "KR",
                "KOR",
                "410",
                "+82",
            )
        },
        "KW" to {
            Country(
                "Kuwait",
                "KW",
                "KWT",
                "414",
                "+965",
            )
        },
        "KY" to {
            Country(
                "Cayman Islands",
                "KY",
                "CYM",
                "136",
                "+1",
            )
        },
        "KZ" to {
            Country(
                "Kazakhstan",
                "KZ",
                "KAZ",
                "398",
                "+7",
            )
        },
        "LA" to {
            Country(
                "Lao People's Democratic Republic",
                "LA",
                "LAO",
                "418",
                "+856",
            )
        },
        "LB" to {
            Country(
                "Lebanon",
                "LB",
                "LBN",
                "422",
                "+961",
            )
        },
        "LC" to {
            Country(
                "Saint Lucia",
                "LC",
                "LCA",
                "662",
                "+1",
            )
        },
        "LI" to {
            Country(
                "Liechtenstein",
                "LI",
                "LIE",
                "438",
                "+423",
            )
        },
        "LK" to {
            Country(
                "Sri Lanka",
                "LK",
                "LKA",
                "144",
                "+94",
            )
        },
        "LR" to {
            Country(
                "Liberia",
                "LR",
                "LBR",
                "430",
                "+231",
            )
        },
        "LS" to {
            Country(
                "Lesotho",
                "LS",
                "LSO",
                "426",
                "+266",
            )
        },
        "LT" to {
            Country(
                "Lithuania",
                "LT",
                "LTU",
                "440",
                "+370",
            )
        },
        "LU" to {
            Country(
                "Luxembourg",
                "LU",
                "LUX",
                "442",
                "+352",
            )
        },
        "LV" to {
            Country(
                "Latvia",
                "LV",
                "LVA",
                "428",
                "+371",
            )
        },
        "LY" to {
            Country(
                "Libya",
                "LY",
                "LBY",
                "434",
                "+218",
            )
        },
        "MA" to {
            Country(
                "Morocco",
                "MA",
                "MAR",
                "504",
                "+212",
            )
        },
        "MC" to {
            Country(
                "Monaco",
                "MC",
                "MCO",
                "492",
                "+377",
            )
        },
        "MD" to {
            Country(
                "Moldova, Republic Of",
                "MD",
                "MDA",
                "498",
                "+373",
            )
        },
        "ME" to {
            Country(
                "Montenegro",
                "ME",
                "MNE",
                "499",
                "+382",
            )
        },
        "MF" to {
            Country(
                "Saint Martin",
                "MF",
                "MAF",
                "663",
                "+590",
            )
        },
        "MG" to {
            Country(
                "Madagascar",
                "MG",
                "MDG",
                "450",
                "+261",
            )
        },
        "MH" to {
            Country(
                "Marshall Islands",
                "MH",
                "MHL",
                "584",
                "+692",
            )
        },
        "MK" to {
            Country(
                "Macedonia (FYROM)",
                "MK",
                "MKD",
                "807",
                "+389",
            )
        },
        "ML" to {
            Country(
                "Mali",
                "ML",
                "MLI",
                "466",
                "+223",
            )
        },
        "MM" to {
            Country(
                "Myanmar",
                "MM",
                "MMR",
                "104",
                "+95",
            )
        },
        "MN" to {
            Country(
                "Mongolia",
                "MN",
                "MNG",
                "496",
                "+976",
            )
        },
        "MO" to {
            Country(
                "Macau",
                "MO",
                "MAC",
                "446",
                "+853",
            )
        },
        "MP" to {
            Country(
                "Northern Mariana Islands",
                "MP",
                "MNP",
                "580",
                "+1",
            )
        },
        "MQ" to {
            Country(
                "Martinique",
                "MQ",
                "MTQ",
                "474",
                "+596",
            )
        },
        "MR" to {
            Country(
                "Mauritania",
                "MR",
                "MRT",
                "478",
                "+222",
            )
        },
        "MS" to {
            Country(
                "Montserrat",
                "MS",
                "MSR",
                "500",
                "+1",
            )
        },
        "MT" to {
            Country(
                "Malta",
                "MT",
                "MLT",
                "470",
                "+356",
            )
        },
        "MU" to {
            Country(
                "Mauritius",
                "MU",
                "MUS",
                "480",
                "+230",
            )
        },
        "MV" to {
            Country(
                "Maldives",
                "MV",
                "MDV",
                "462",
                "+960",
            )
        },
        "MW" to {
            Country(
                "Malawi",
                "MW",
                "MWI",
                "454",
                "+265",
            )
        },
        "MX" to {
            Country(
                "Mexico",
                "MX",
                "MEX",
                "484",
                "+52",
            )
        },
        "MY" to {
            Country(
                "Malaysia",
                "MY",
                "MYS",
                "458",
                "+60",
            )
        },
        "MZ" to {
            Country(
                "Mozambique",
                "MZ",
                "MOZ",
                "508",
                "+258",
            )
        },
        "NA" to {
            Country(
                "Namibia",
                "NA",
                "NAM",
                "516",
                "+264",
            )
        },
        "NC" to {
            Country(
                "New Caledonia",
                "NC",
                "NCL",
                "540",
                "+687",
            )
        },
        "NE" to {
            Country(
                "Niger",
                "NE",
                "NER",
                "562",
                "+227",
            )
        },
        "NF" to {
            Country(
                "Norfolk Islands",
                "NF",
                "NFK",
                "574",
                "+672",
            )
        },
        "NG" to {
            Country(
                "Nigeria",
                "NG",
                "NGA",
                "566",
                "+234",
            )
        },
        "NI" to {
            Country(
                "Nicaragua",
                "NI",
                "NIC",
                "558",
                "+505",
            )
        },
        "NL" to {
            Country(
                "Netherlands",
                "NL",
                "NLD",
                "528",
                "+31",
            )
        },
        "NO" to {
            Country(
                "Norway",
                "NO",
                "NOR",
                "578",
                "+47",
            )
        },
        "NP" to {
            Country(
                "Nepal",
                "NP",
                "NPL",
                "524",
                "+977",
            )
        },
        "NR" to {
            Country(
                "Nauru",
                "NR",
                "NRU",
                "520",
                "+674",
            )
        },
        "NU" to {
            Country(
                "Niue",
                "NU",
                "NIU",
                "570",
                "+683",
            )
        },
        "NZ" to {
            Country(
                "New Zealand",
                "NZ",
                "NZL",
                "554",
                "+64",
            )
        },
        "OM" to {
            Country(
                "Oman",
                "OM",
                "OMN",
                "512",
                "+968",
            )
        },
        "PA" to {
            Country(
                "Panama",
                "PA",
                "PAN",
                "591",
                "+507",
            )
        },
        "PE" to {
            Country(
                "Peru",
                "PE",
                "PER",
                "604",
                "+51",
            )
        },
        "PF" to {
            Country(
                "French Polynesia",
                "PF",
                "PYF",
                "258",
                "+689",
            )
        },
        "PG" to {
            Country(
                "Papua New Guinea",
                "PG",
                "PNG",
                "598",
                "+675",
            )
        },
        "PH" to {
            Country(
                "Philippines",
                "PH",
                "PHL",
                "608",
                "+63",
            )
        },
        "PK" to {
            Country(
                "Pakistan",
                "PK",
                "PAK",
                "586",
                "+92",
            )
        },
        "PL" to {
            Country(
                "Poland",
                "PL",
                "POL",
                "616",
                "+48",
            )
        },
        "PM" to {
            Country(
                "Saint Pierre And Miquelon",
                "PM",
                "SPM",
                "666",
                "+508",
            )
        },
        "PN" to {
            Country(
                "Pitcairn Islands",
                "PN",
                "PCN",
                "612",
                "+870",
            )
        },
        "PR" to {
            Country(
                "Puerto Rico",
                "PR",
                "PRI",
                "630",
                "+1",
            )
        },
        "PS" to {
            Country(
                "Palestine",
                "PS",
                "PSE",
                "275",
                "+970",
            )
        },
        "PT" to {
            Country(
                "Portugal",
                "PT",
                "PRT",
                "620",
                "+351",
            )
        },
        "PW" to {
            Country(
                "Palau",
                "PW",
                "PLW",
                "585",
                "+680",
            )
        },
        "PY" to {
            Country(
                "Paraguay",
                "PY",
                "PRY",
                "600",
                "+595",
            )
        },
        "QA" to {
            Country(
                "Qatar",
                "QA",
                "QAT",
                "634",
                "+974",
            )
        },
        "RE" to {
            Country(
                "Réunion",
                "RE",
                "REU",
                "638",
                "+262",
            )
        },
        "RO" to {
            Country(
                "Romania",
                "RO",
                "ROU",
                "642",
                "+40",
            )
        },
        "RS" to {
            Country(
                "Serbia",
                "RS",
                "SRB",
                "688",
                "+381",
            )
        },
        "RU" to {
            Country(
                "Russian Federation",
                "RU",
                "RUS",
                "643",
                "+7",
            )
        },
        "RW" to {
            Country(
                "Rwanda",
                "RW",
                "RWA",
                "646",
                "+250",
            )
        },
        "SA" to {
            Country(
                "Saudi Arabia",
                "SA",
                "SAU",
                "682",
                "+966",
            )
        },
        "SB" to {
            Country(
                "Solomon Islands",
                "SB",
                "SLB",
                "090",
                "+677",
            )
        },
        "SC" to {
            Country(
                "Seychelles",
                "SC",
                "SYC",
                "690",
                "+248",
            )
        },
        "SD" to {
            Country(
                "Sudan",
                "SD",
                "SDN",
                "729",
                "+249",
            )
        },
        "SE" to {
            Country(
                "Sweden",
                "SE",
                "SWE",
                "752",
                "+46",
            )
        },
        "SG" to {
            Country(
                "Singapore",
                "SG",
                "SGP",
                "702",
                "+65",
            )
        },
        "SH" to {
            Country(
                "Saint Helena, Ascension And Tristan Da Cunha",
                "SH",
                "SHN",
                "654",
                "+290",
            )
        },
        "SI" to {
            Country(
                "Slovenia",
                "SI",
                "SVN",
                "705",
                "+386",
            )
        },
        "SK" to {
            Country(
                "Slovakia",
                "SK",
                "SVK",
                "703",
                "+421",
            )
        },
        "SL" to {
            Country(
                "Sierra Leone",
                "SL",
                "SLE",
                "694",
                "+232",
            )
        },
        "SM" to {
            Country(
                "San Marino",
                "SM",
                "SMR",
                "674",
                "+378",
            )
        },
        "SN" to {
            Country(
                "Senegal",
                "SN",
                "SEN",
                "686",
                "+221",
            )
        },
        "SO" to {
            Country(
                "Somalia",
                "SO",
                "SOM",
                "706",
                "+252",
            )
        },
        "SR" to {
            Country(
                "Suriname",
                "SR",
                "SUR",
                "740",
                "+597",
            )
        },
        "SS" to {
            Country(
                "South Sudan",
                "SS",
                "SSD",
                "728",
                "+211",
            )
        },
        "ST" to {
            Country(
                "Sao Tome And Principe",
                "ST",
                "STP",
                "678",
                "+239",
            )
        },
        "SV" to {
            Country(
                "El Salvador",
                "SV",
                "SLV",
                "222",
                "+503",
            )
        },
        "SX" to {
            Country(
                "Sint Maarten",
                "SX",
                "SXM",
                "534",
                "+1",
            )
        },
        "SY" to {
            Country(
                "Syrian Arab Republic",
                "SY",
                "SYR",
                "760",
                "+963",
            )
        },
        "SZ" to {
            Country(
                "Swaziland",
                "SZ",
                "SWZ",
                "748",
                "+268",
            )
        },
        "TC" to {
            Country(
                "Turks and Caicos Islands",
                "TC",
                "TCA",
                "796",
                "+1",
            )
        },
        "TD" to {
            Country(
                "Chad",
                "TD",
                "TCD",
                "148",
                "+235",
            )
        },
        "TG" to {
            Country(
                "Togo",
                "TG",
                "TGO",
                "768",
                "+228",
            )
        },
        "TH" to {
            Country(
                "Thailand",
                "TH",
                "THA",
                "764",
                "+66",
            )
        },
        "TJ" to {
            Country(
                "Tajikistan",
                "TJ",
                "TJK",
                "762",
                "+992",
            )
        },
        "TK" to {
            Country(
                "Tokelau",
                "TK",
                "TKL",
                "772",
                "+690",
            )
        },
        "TL" to {
            Country(
                "Timor-leste",
                "TL",
                "TLS",
                "626",
                "+670",
            )
        },
        "TM" to {
            Country(
                "Turkmenistan",
                "TM",
                "TKM",
                "795",
                "+993",
            )
        },
        "TN" to {
            Country(
                "Tunisia",
                "TN",
                "TUN",
                "788",
                "+216",
            )
        },
        "TO" to {
            Country(
                "Tonga",
                "TO",
                "TON",
                "776",
                "+676",
            )
        },
        "TR" to {
            Country(
                "Turkey",
                "TR",
                "TUR",
                "792",
                "+90",
            )
        },
        "TT" to {
            Country(
                "Trinidad &amp; Tobago",
                "TT",
                "TTO",
                "780",
                "+1",
            )
        },
        "TV" to {
            Country(
                "Tuvalu",
                "TV",
                "TUV",
                "798",
                "+688",
            )
        },
        "TW" to {
            Country(
                "Taiwan",
                "TW",
                "TWN",
                "158",
                "+886",
            )
        },
        "TZ" to {
            Country(
                "Tanzania, United Republic Of",
                "TZ",
                "TZA",
                "834",
                "+255",
            )
        },
        "UA" to {
            Country(
                "Ukraine",
                "UA",
                "UKR",
                "804",
                "+380",
            )
        },
        "UG" to {
            Country(
                "Uganda",
                "UG",
                "UGA",
                "800",
                "+256",
            )
        },
        "US" to {
            Country(
                "United States",
                "US",
                "USA",
                "840",
                "+1",
            )
        },
        "UY" to {
            Country(
                "Uruguay",
                "UY",
                "URY",
                "858",
                "+598",
            )
        },
        "UZ" to {
            Country(
                "Uzbekistan",
                "UZ",
                "UZB",
                "860",
                "+998",
            )
        },
        "VA" to {
            Country(
                "Holy See (vatican City State)",
                "VA",
                "VAT",
                "336",
                "+379",
            )
        },
        "VC" to {
            Country(
                "Saint Vincent &amp; The Grenadines",
                "VC",
                "VCT",
                "670",
                "+1",
            )
        },
        "VE" to {
            Country(
                "Venezuela, Bolivarian Republic Of",
                "VE",
                "VEN",
                "862",
                "+58",
            )
        },
        "VG" to {
            Country(
                "British Virgin Islands",
                "VG",
                "VGB",
                "092",
                "+1",
            )
        },
        "VI" to {
            Country(
                "US Virgin Islands",
                "VI",
                "VIR",
                "850",
                "+1",
            )
        },
        "VN" to {
            Country(
                "Vietnam",
                "VN",
                "VNM",
                "704",
                "+84",
            )
        },
        "VU" to {
            Country(
                "Vanuatu",
                "VU",
                "VUT",
                "548",
                "+678",
            )
        },
        "WF" to {
            Country(
                "Wallis And Futuna",
                "WF",
                "WLF",
                "876",
                "+681",
            )
        },
        "WS" to {
            Country(
                "Samoa",
                "WS",
                "WSM",
                "882",
                "4685",
            )
        },
        "XK" to {
            Country(
                "Kosovo",
                "XK",
                "XKX",
                "926",
                "+383",
            )
        },
        "YE" to {
            Country(
                "Yemen",
                "YE",
                "YEM",
                "887",
                "+967",
            )
        },
        "YT" to {
            Country(
                "Mayotte",
                "YT",
                "MYT",
                "175",
                "+262",
            )
        },
        "ZA" to {
            Country(
                "South Africa",
                "ZA",
                "ZAF",
                "710",
                "+27",
            )
        },
        "ZM" to {
            Country(
                "Zambia",
                "ZM",
                "ZMB",
                "894",
                "+260",
            )
        },
        "ZW" to {
            Country(
                "Zimbabwe",
                "ZW",
                "ZWE",
                "716",
                "+263",
            )
        },
        "GG" to {
            Country(
                "Aland",
                "GG",
                "GGY",
                "831",
                "+44-1481",
            )
        },
        "IL" to {
            Country(
                "Israel",
                "IL",
                "ISR",
                "376",
                "+972",
            )
        },
        "GS" to {
            Country(
                "South Georgia",
                "GS",
                "SGS",
                "239",
                "+500",
            )
        },
    )
