<!DOCTYPE html>
<html lang="en">
<head>
    <title>Web Socket Client Example</title>
    {!<script src="script/jquery-3.6.3.min.js"></script>!}
    <script type="text/javascript">
        window.onload = function () {
            var conn;
            var log = document.getElementById("log");
            var msg = document.getElementById("msg");

            function logItem(text, color) {
                var item = document.createElement("pre");
                if (color) item.style.color = color
                item.innerText = text;
                return item
            }

            function appendLog(item) {
                var doScroll = log.scrollTop === log.scrollHeight - log.clientHeight;
                log.appendChild(item);
                if (doScroll) {
                    log.scrollTop = log.scrollHeight - log.clientHeight;
                }
            }

            function connected() {
                appendLog(logItem('Connected.', 'green'))
                document.getElementById("disconnect").disabled = false
                document.getElementById("connect").disabled = true
                document.getElementById("status").style.color = "green"
                document.getElementById("status").innerHTML = "CONNECTED"
                document.getElementById("msg").disabled = false
                document.getElementById("sendBtn").disabled = false
            }

            function disconnected(event) {
                document.getElementById("connect").disabled = false
                document.getElementById("disconnect").disabled = true
                document.getElementById("status").style.color = "red"
                document.getElementById("status").innerHTML = "DISCONNECTED"
                document.getElementById("msg").disabled = true
                document.getElementById("msg").innerText = ""
                document.getElementById("sendBtn").disabled = true
                var reason;
                var color;
                if (event.code === 1000) {
                    color = "green";
                    reason = "Disconnected.";
                } else if (event.code === 1001) {
                    color = "red";
                    reason = "An endpoint is \"going away\", such as a server going down or a browser having navigated away from a page.";
                } else if (event.code === 1002) {
                    color = "red";
                    reason = "An endpoint is terminating the connection due to a protocol error";
                } else if (event.code === 1003) {
                    color = "red";
                    reason = "An endpoint is terminating the connection because it has received a type of data it cannot accept (e.g., an endpoint that understands only text data MAY send this if it receives a binary message).";
                } else if (event.code === 1004) {
                    color = "red";
                    reason = "Reserved. The specific meaning might be defined in the future.";
                } else if (event.code === 1005) {
                    color = "red";
                    reason = "No status code was actually present.";
                } else if (event.code === 1006) {
                    color = "red";
                    reason = "The connection was closed abnormally, e.g., without sending or receiving a Close control frame";
                } else if (event.code === 1007) {
                    color = "red";
                    reason = "An endpoint is terminating the connection because it has received data within a message that was not consistent with the type of the message (e.g., non-UTF-8 [https://www.rfc-editor.org/rfc/rfc3629] data within a text message).";
                } else if (event.code === 1008) {
                    color = "red";
                    reason = "An endpoint is terminating the connection because it has received a message that \"violates its policy\". This reason is given either if there is no other sutible reason, or if there is a need to hide specific details about the policy.";
                } else if (event.code === 1009) {
                    color = "red";
                    reason = "An endpoint is terminating the connection because it has received a message that is too big for it to process.";
                } else if (event.code === 1010) {
                    // Note that this status code is not used by the server, because it can fail the WebSocket handshake instead.
                    color = "red";
                    reason = "An endpoint (client) is terminating the connection because it has expected the server to negotiate one or more extension, but the server didn't return them in the response message of the WebSocket handshake. \n Specifically, the extensions that are needed are: " + event.reason;
                } else if (event.code === 1011) {
                    color = "red";
                    reason = "A server is terminating the connection because it encountered an unexpected condition that prevented it from fulfilling the request.";
                } else if (event.code === 1015) {
                    color = "red";
                    reason = "The connection was closed due to a failure to perform a TLS handshake (e.g., the server certificate can't be verified).";
                } else {
                    color = "red";
                    reason = "Unknown reason";
                }
                appendLog(logItem(reason, color));
            }

            document.getElementById("connect").onclick = function () {
                var server = document.getElementById("wsURL");
                conn = new WebSocket(server.value);
                if (window["WebSocket"]) {
                    if (conn) {
                        conn.onopen = connected
                        conn.onclose = disconnected
                        conn.onerror = function () {
                            appendLog(logItem('There was an error with your websocket', 'red'))
                        }
                        conn.onmessage = function (evt) {
                            appendLog(logItem('\nResponse:', 'black'));
                            var messages = evt.data.split('\n');
                            for (var i = 0; i < messages.length; i++) {
                                appendLog(logItem(messages[i], 'black'));
                            }
                        }
                    }
                } else {
                    appendLog(logItem("Your browser does not support WebSockets.", 'red'));
                }
            };

            document.getElementById("disconnect").onclick = function () {
                conn.close()
                disconnected()
            };

            document.getElementById("form").onsubmit = function (e) {
                e.preventDefault();
                if (!conn) {
                    return false;
                }
                if (!msg.value) {
                    appendLog(logItem('Message is empty!', 'red'));
                } else {
                    conn.send(msg.value);
                    appendLog(logItem('Request:\n' + msg.value, 'green'));
                }
                return false;
            };
            document.getElementById("clearLogBtn").onclick = function (e) {
                log.innerHTML = "";
            }
        };

    </script>
    <style type="text/css">
        html {
            overflow: hidden;
        }

        body {
            overflow: hidden;
            padding: 0;
            margin: 0;
            width: 100%;
            height: 100%;
            background: darkgray;
        }

        #log {
            background: white;
            margin: 0;
            padding: 0.5em 0.5em 0.5em 0.5em;
            top: 1.5em;
            left: 0.5em;
            right: 0.5em;
            bottom: 3em;
            overflow: auto;
            position: absolute;
            height: 530px;
        }

        #form {
            padding: 0 0.5em 0 0.5em;
            margin: 0;
            position: absolute;
            bottom: 3em;
            top: 5em;
            left: 8px;
            width: 100%;
            overflow: hidden;

        }

        #serverLocation {
            padding-top: 0.3em;
        }

        #requestSection {
            height: 38px;
        }

        #responseMsgSection {
            height: 570px;
            position: relative;
        }
    </style>
</head>
<body>
<fieldset id="serverLocation">
    <legend>Server address</legend>
    <div>
        <input type="button" id="connect" value="Connect"/>
        <input type="button" id="disconnect" value="Disconnect" disabled/>
        <input type="text" id="wsURL" value="${baseAddress}" placeholder="ws 'or' wss://ip:port/" size="64">
        <span id="status" style="color: red"></span>
    </div>
</fieldset>
<fieldset id="requestSection">
    <legend>Message</legend>
    <form id="form">
        <input type="submit" id="sendBtn" value="Send" disabled/>
        <input type="text" id="msg" size="80" disabled/>
    </form>
</fieldset>
<fieldset id="responseMsgSection">
    <legend>Exchange log</legend>
    <div id="log"></div>
</fieldset>
<fieldset id="clearResponseMsgSection">
    <input type="button" id="clearLogBtn" value="Clear"/>
</fieldset>
</body>
</html>