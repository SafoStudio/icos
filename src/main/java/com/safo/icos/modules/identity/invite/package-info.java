/**
 * Invitation module.
 * <p>
 * Manages the lifecycle of user invitations to join a company:
 * <ul>
 *   <li><strong>Create</strong> — ADMIN/OWNER/HR sends an invite with a role and department.</li>
 *   <li><strong>View</strong> — invitee views invite details via a secure token link.</li>
 *   <li><strong>Resend</strong> — generates a new token and extends the expiration.</li>
 *   <li><strong>Cancel</strong> — revokes a pending invitation.</li>
 * </ul>
 * <p>
 * Invitation tokens are stored as hashes; raw tokens are delivered via email.
 * Acceptance of an invite triggers user registration in the {@code auth} module.
 * <p>
 * <h3>Module layers</h3>
 * <ul>
 *   <li>{@code domain} — {@code Invite} entity, {@code InviteStatus}, {@code InviteRepository} port.</li>
 *   <li>{@code application} — {@code InviteService} orchestrating invite workflows.</li>
 *   <li>{@code infrastructure} — JPA adapter, email service, mapper.</li>
 * </ul>
 *
 * @see com.safo.icos.modules.identity.invite.application.InviteService
 * @see com.safo.icos.modules.identity.invite.domain.model.Invite
 * @see com.safo.icos.modules.identity.auth.AuthModule
 */
package com.safo.icos.modules.identity.invite;
