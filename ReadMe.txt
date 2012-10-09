Velox Chat

This is an instant messenger controlled at one end by a server,and users can login from anywhere,including the server's end.

Designed By:
Ashwin Jiwane 05005017
Avijit Satoskar 05d05012
Sumedh Ambokar 05d05013
Hemendra Srivastava 05d05014

1.To run the project in its full flow you will need at least 2 computers,one for the server,say nsl-03,from where we run the server and one user can login from the same computer,and another one for some other user,say nsl-02.
Make a project of the folder Project1 in eclipse.include postgresql.jar in java build path in properties of the project if required.

2.Now,assuming that the server is run on nsl-03, you have to make changes in line no.s 215, 216 and 217 in Login.java, which are currently:

	serverSocket = new Socket("10.105.11.03",5678);
	Socket filetransfersocket=new Socket("10.105.11.03",8888);
	Socket replyfromfileacceptor=new Socket("10.105.11.03",8889);

 for the user which logs in from nsl-03 itself,changing "10.105.11.03" to "localhost" in Login.java used on nsl-03.

If some other nsl machine is used to run the server, also change 10.105.11.03 to 10.105.11.XX where nsl-XX is used to run the server.

3.To start the server from the server computer,run VServer.java

4.To Login from any computer,run Login.java.You can either create a new user-id or use existing ones from database which are currently:

username    password
  a            a
  q            q

5.You can add a friend,remove a friend,show/hide offline contacts,delete your account,set any status message of your choice,sign out,view help and about in the main vchat window,which also shows your friend list.

6.By double clicking on your friend's name, you can open an instant message window,which has a text bar to enter your message,a smiley button from which you can choose a smiley to insert into your message,a buzz button and a file transfer button.
If the friend is online, even if invisible, a window pops up at his end,showing your message,and then you can chat.
If he is offline,the message is sent to him as an offline message,which he receives when he logs in next time.

7.Only text files can be sent completely without any glitches.On completion of file transfer,both parties are informed of success.

8.Many sounds have been added to the project which unfortunately cannot be heard on nsl machines.
When a user logs in,a sound is played at his end.If he has not signed as invisible,all his friends get a sound and pop up window alert that he has logged in.Similar alert is played when a friend logs out or becomes invisible.
On sending the first message to a friend,a window pops up at the friend's end with a sound.If someone receives a message in an already open window which is not focussed currently,a sound alert is made.
The buzz button plays sound at both ends.