package hoot.app.Servlets.Api.V1.User;

import com.fasterxml.jackson.databind.JsonNode;
import hoot.app.Servlets.Api.V1.AbstractApiServlet;
import hoot.model.entities.Follower;
import hoot.model.entities.User;
import hoot.model.query.api.IsValidUserSession;
import hoot.model.repositories.FollowerRepository;
import hoot.system.Security.AuthenticationRequired;
import hoot.system.Exception.CouldNotSaveException;
import hoot.system.objects.ObjectManager;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@AuthenticationRequired
@WebServlet({"/api/V1/user/me/follow"})
public class MeFollowApiServlet extends AbstractApiServlet
{
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
        JsonNode node = (JsonNode) this.deserializeJsonRequestBody(request, JsonNode.class);

        Integer userIdToFollow = node.asInt();
        Integer currentUserId = (Integer) request
                .getSession(true)
                .getAttribute(IsValidUserSession.SESSION_USER_IDENTIFIER);

        FollowerRepository repository     = (FollowerRepository) ObjectManager.get(FollowerRepository.class);
        Follower           followerEntity = (Follower) ObjectManager.create(Follower.class);

        followerEntity.user    = (User) ObjectManager.create(User.class);
        followerEntity.follows = (User) ObjectManager.create(User.class);

        followerEntity.user.id    = currentUserId;
        followerEntity.follows.id = userIdToFollow;

        try {
            repository.save(followerEntity);
            this.sendResponse(response, HttpServletResponse.SC_OK, this.serialize(true));
        } catch (CouldNotSaveException e) {
            this.sendResponse(response, HttpServletResponse.SC_CONFLICT, this.serialize(false));
        }
    }
}
