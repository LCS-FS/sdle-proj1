# Demo Video
[Youtube link](https://www.youtube.com/watch?v=l0T-HqJjn0U&feature=youtu.be)

# Dependencies
- local:
  - Python 3
  - Pip 
  - Django: `pip install django`
- cloud:
  - Java 17+
  - Gradle

# Instructions
- Local:
  - `cd local`
  - `python manage.py runserver` - runs on port 8000
  - `python manage.py runserver <port>` - runs on specified port
  - access 127.0.0.1:port
- Cloud:
  - `cd cloud`
  - `./runProxy.sh` - initializes proxy at port 12345
  - `./runNode.sh` - initializes node at a random port, at least 2 nodes required
  - gradle marks it as ready at 85%
