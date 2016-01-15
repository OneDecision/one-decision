/*******************************************************************************
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package io.onedecision.engine.decisions.web;

import io.onedecision.engine.decisions.api.DecisionException;
import io.onedecision.engine.usermgmt.model.Profile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Return role and other profile information about the current user.
 * 
 * @author Tim Stephenson
 */
@Controller
@RequestMapping("/profile")
public class UserProfileController {

    protected static final Logger LOGGER = LoggerFactory
            .getLogger(UserProfileController.class);

    @RequestMapping(method = RequestMethod.GET, value = "/", headers = "Accept=application/json")
    @ResponseBody
    public final Profile getProfile(Authentication auth)
            throws DecisionException {
        LOGGER.info(String.format("getProfile"));

        Profile profile = new Profile();
        profile.setUsername(auth.getName());
        for (GrantedAuthority authority : auth.getAuthorities()) {
            // trim ROLE_prefix
            profile.getRoles().add(authority.getAuthority().substring(5));
        }

        return profile;
    }

}
