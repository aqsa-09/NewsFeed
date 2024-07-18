import java.net.StandardSocketOptions;
import java.util.*;
import java.text.SimpleDateFormat;

class SocialNetwork {
    private Map<String, User> users;
    private List<Post> posts;
    private User currentUser;

    public SocialNetwork() {
        users = new HashMap<>();
        posts = new ArrayList<>();
    }

    public void signUp(String username) {
        if (!users.containsKey(username)) {
            users.put(username, new User(username));
            System.out.println(username + " signed up successfully.");
        }
        else{
            System.out.println("Username " + username + " is already taken.");
        }
    }

    public void login(String username) {
        if (users.containsKey(username)) {
            currentUser = users.get(username);
            System.out.println(username + " logged in.");
//            showNewsFeed();
        }
        else{
            System.out.println("Username " + username + " does not exist.");
        }
    }

    public void post(String content) {
        if (currentUser != null) {
            Post newPost = new Post(currentUser, content);
            posts.add(newPost);
            currentUser.addPost(newPost);
            System.out.println("Post created with id: " + newPost.getId());
        }
        else{
            System.out.println("Please log in to post.");
        }
    }

    public void follow(String usernameToFollow) {
        if (currentUser != null) {
            if (users.containsKey(usernameToFollow)) {
                User userToFollow = users.get(usernameToFollow);
                currentUser.follow(userToFollow);
                System.out.println(currentUser.getUsername() + " is now following " + usernameToFollow);
            }
            else{
                System.out.println("Username " + usernameToFollow + " does not exist.");
            }
        }
        else{
            System.out.println("Please log in to follow someone.");
        }
    }

    public void reply(String postId, String content) {
        if (currentUser != null) {
            Post postToReply = getPostById(postId);
            if (postToReply != null) {
                Comment newComment = new Comment(currentUser, content);
                postToReply.addComment(newComment);
                System.out.println("Reply added to post id: " + postId);
            }
            else{
                System.out.println("Post with id " + postId + " does not exist.");
            }
        }
        else{
            System.out.println("Please log in to reply.");
        }
    }

    public void upvote(String postId) {
        if (currentUser != null) {
            Post postToUpvote = getPostById(postId);
            if (postToUpvote != null) {
                postToUpvote.upvote();
                System.out.println("Post with id " + postId + " upvoted.");
            } else {
                System.out.println("Post with id " + postId + " does not exist.");
            }
        }
        else{
            System.out.println("Please log in to upvote.");
        }
    }

    public void downvote(String postId) {
        if (currentUser != null) {
            Post postToDownvote = getPostById(postId);
            if (postToDownvote != null) {
                postToDownvote.downvote();
                System.out.println("Post with id " + postId + " downvoted.");
            }
            else{
                System.out.println("Post with id " + postId + " does not exist.");
            }
        }
        else{
            System.out.println("Please log in to downvote.");
        }
    }

    public void showNewsFeed() {
        if (currentUser != null) {
            List<Post> newsFeed = new ArrayList<>(currentUser.getPosts());
            for (User followedUser : currentUser.getFollowedUsers()) {
                newsFeed.addAll(followedUser.getPosts());
            }
            for (Post post : newsFeed) {
                post.display();
            }
        }
        else{
            System.out.println("Please log in to view the news feed.");
        }
    }

    private Post getPostById(String postId) {
        for (Post post : posts) {
            if (post.getId().equals(postId)) {
                return post;
            }
        }
        return null;
    }

    public static void main(String[] args) {
        SocialNetwork sn = new SocialNetwork();
        Scanner scanner = new Scanner(System.in);

        while (scanner.hasNextLine()) {
            String input = scanner.nextLine();
            String[] parts = input.split(" ");
            String command = parts[0];

            switch (command) {
                case "signup":
                    sn.signUp(parts[1]);
                    break;
                case "login":
                    sn.login(parts[1]);
                    break;
                case "post":
                    sn.post(parts[1]);
                    break;
                case "follow":
                    sn.follow(parts[1]);
                    break;
                case "reply":
                    sn.reply(parts[1], parts[2]);
                    break;
                case "upvote":
                    sn.upvote(parts[1]);
                    break;
                case "downvote":
                    sn.downvote(parts[1]);
                    break;
                case "shownewsfeed":
                    sn.showNewsFeed();
                    break;
                default:
                    System.out.println("Invalid command.");
                    break;
            }
        }

        scanner.close();
    }
}

class User {
    private String username;
    private List<User> followedUsers;
    private List<Post> posts;

    public User(String username) {
        this.username = username;
        this.followedUsers = new ArrayList<>();
        this.posts = new ArrayList<>();
    }

    public String getUsername() {
        return username;
    }

    public List<User> getFollowedUsers() {
        return followedUsers;
    }

    public void follow(User user) {
        if (!followedUsers.contains(user)) {
            followedUsers.add(user);
        }
    }

    public void addPost(Post post) {
        posts.add(post);
    }

    public List<Post> getPosts() {
        return posts;
    }
}

class Post {
    private static int counter = 0;
    private String id;
    private User user;
    private String content;
    private int upvotes;
    private int downvotes;
    private Date timestamp;
    private List<Comment> comments;

    public Post(User user, String content) {
        this.id = String.format("%3d", ++counter);
        this.user = user;
        this.content = content;
        this.upvotes = 0;
        this.downvotes = 0;
        this.timestamp = new Date();
        this.comments = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void upvote() {
        upvotes++;
    }

    public void downvote() {
        downvotes++;
    }

    public void addComment(Comment comment) {
        comments.add(comment);
    }

    public void display() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println("id: " + id);
        System.out.println("(" + upvotes + " upvotes, " + downvotes + " downvotes)");
        System.out.println(user.getUsername());
        System.out.println(content);
        System.out.println(sdf.format(timestamp));
        for (Comment comment : comments) {
            comment.display();
        }
    }
}

class Comment {
    private User user;
    private String content;
    private Date timestamp;

    public Comment(User user, String content) {
        this.user = user;
        this.content = content;
        this.timestamp = new Date();
    }

    public void display() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println("\tid: ");
        System.out.println("\t" + user.getUsername());
        System.out.println("\t" + content);
        System.out.println("\t" + sdf.format(timestamp));
    }
}
