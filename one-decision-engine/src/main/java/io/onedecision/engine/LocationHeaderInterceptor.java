/*******************************************************************************
 * Copyright owned by Tim Stephenson and other contributors. 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Tim Stephenson - initial API and implementation
 *******************************************************************************/
package io.onedecision.engine;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
public class LocationHeaderInterceptor extends HandlerInterceptorAdapter {
	// @Override
	// public void postHandle( HttpServletRequest request, HttpServletResponse
	// response,
	// Object handler, ModelAndView modelAndView) throws Exception {
	// System.out.println("---method executed---");
	// }
	@Override
	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		System.out.println("---Request Completed---");
		if (response.getStatus() == HttpStatus.CREATED.value()) {
			response.setHeader("Location",
					String.format("%1$s", request.getRequestURL()));
		}
	}
} 