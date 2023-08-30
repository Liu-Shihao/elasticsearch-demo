const http = require('http');
const fs = require('fs');
const path = require('path');


const server = http.createServer((req, res) => {
    const filePath = path.join(__dirname, 'faq.html');
    
    fs.readFile(filePath, 'utf8', (err, content) => {
        if (err) {
            res.writeHead(500, {'Content-Type': 'text/plain'});
            res.end('Internal Server Error');
        } else {
            res.writeHead(200, {'Content-Type': 'text/html'});
            res.end(content);
        }
    });
});

const PORT = 3000;
server.listen(PORT, () => {
    console.log(`Server is running at http://localhost:${PORT}`);
});
