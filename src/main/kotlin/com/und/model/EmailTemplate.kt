package com.und.model

import javax.persistence.*
//import javax.validation.constraints.Email
import javax.validation.constraints.NotNull
import javax.validation.constraints.Null

@Entity
@Table(name = "email_template")
class EmailTemplate {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "email_template_id_seq")
    @SequenceGenerator(name = "email_template_id_seq", sequenceName = "email_template_id_seq", allocationSize = 1)
    var id: Long? = null

    @Column(name = "client_id")
    @NotNull
    var clientID: Long? = null

    @Column(name = "appuser_id")
    @NotNull
    var appuserID: Long? = null

    @Column(name = "email_template_body")
    @NotNull
    lateinit var emailTemplateBody: String

    @Column(name = "email_template_subject")
    lateinit var emailTemplateSubject: String

    @Column(name = "parent_id")
    @Null
    var parentID: Long? = null

    //@Email
    @Column(name = "from_user")
    @NotNull
    lateinit var from: String

    @Column(name = "message_type") //Promotional or Transactional
    @NotNull
    @Enumerated(EnumType.STRING)
    var messageType: EmailMessageType? = null

    @Column(name = "tags")
    var tags: String? = null
}

