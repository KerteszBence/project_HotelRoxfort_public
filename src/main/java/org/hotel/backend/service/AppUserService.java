package org.hotel.backend.service;





import org.hotel.backend.domain.*;
import org.hotel.backend.dto.*;
import org.hotel.backend.repository.AppUserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.core.io.InputStreamSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.authority.AuthorityUtils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.transaction.Transactional;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;


@Service
@Transactional
public class AppUserService implements UserDetailsService {

    private ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final AppUserRepository appUserRepository;
    private final RoomService roomService;
    private final BookingRoomUserService bookingRoomUserService;
    private final WeatherService weatherService;
    private final EmailService emailService;
    private final ExtraService extraService;
    private final BookingExtraService bookingExtraService;
    private final ExchangeService exchangeService;




    @Autowired
    public AppUserService(ModelMapper modelMapper, PasswordEncoder passwordEncoder,  AppUserRepository appUserRepository, RoomService roomService, BookingRoomUserService bookingRoomUserService, EmailService emailService, ExtraService extraService, BookingExtraService bookingExtraService, WeatherService weatherService, ExchangeService exchangeService) {
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.appUserRepository = appUserRepository;
        this.roomService = roomService;
        this.bookingRoomUserService = bookingRoomUserService;
        this.emailService = emailService;
        this.extraService = extraService;
        this.bookingExtraService = bookingExtraService;
        this.weatherService = weatherService;
        this.exchangeService = exchangeService;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        System.out.println("email" + email);
        AppUser appUser = findUserByEmail(email);

        String[] roles = appUser.getRoles().stream()
                .map(Enum::toString)
                .toArray(String[]::new);

        return User
                .withUsername(appUser.getEmail())
                .authorities(AuthorityUtils.createAuthorityList(roles))
                .password(appUser.getPassword())
                .build();
    }

    public AppUser findUserByEmail(String email) {
        Optional<AppUser> optionalAppUser = appUserRepository.findByEmail(email);
        if (optionalAppUser.isEmpty()) {
            throw new UserEmailNotFoundException(email);
        }
        return optionalAppUser.get();
    }

    public AppUserInfo saveAppUser(AppUserCreateCommand command) throws MessagingException, UnsupportedEncodingException {

        PasswordValidator.validatePassword(command.getPassword());

        if (appUserRepository.existsByEmail(command.getEmail())) {
            throw new DuplicateEmailException(command.getEmail());
        }

        AppUser appUser = new AppUser();
        appUser.setFirstName(command.getFirstName());
        appUser.setLastName(command.getLastName());
        appUser.setPassword(passwordEncoder.encode(command.getPassword()));

        appUser.setCreationDate(LocalDateTime.now());
        appUser.setEmail(command.getEmail());

        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(1);
        String verificationToken = UUID.randomUUID().toString();
        appUser.setVerificationToken(verificationToken);
        appUser.setVerificationTokenExpiration(expirationTime);
        String verificationLink = "http://ec2-3-75-98-195.eu-central-1.compute.amazonaws.com:8080/api/users/verifyEmail?token=" + verificationToken;

        String messageEmail = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Hotel Roxfort - Welcome!</title>\n" +
                "    <style>\n" +
                "        body {\n" +
                "            font-family: 'Arial', sans-serif;\n" +
                "            background-color: #f2f2f2;\n" +
                "            margin: 0;\n" +
                "            padding: 0;\n" +
                "        }\n" +
                "\n" +
                "        .container {\n" +
                "            max-width: 600px;\n" +
                "            margin: 20px auto;\n" +
                "            background-color: #fff;\n" +
                "            border-radius: 8px;\n" +
                "            overflow: hidden;\n" +
                "        }\n" +
                "\n" +
                "        .header {\n" +
                "            background-color: #342929;\n" +
                "            color: #fff;\n" +
                "            text-align: center;\n" +
                "            padding: 20px;\n" +
                "        }\n" +
                "\n" +
                "        .logo {\n" +
                "            max-width: 100px;\n" +
                "            height: auto;\n" +
                "        }\n" +
                "\n" +
                "        h1 {\n" +
                "            margin: 0;\n" +
                "            font-size: 24px;\n" +
                "        }\n" +
                "\n" +
                "        .content {\n" +
                "            padding: 20px;\n" +
                "        }\n" +
                "\n" +
                "        .welcome-text {\n" +
                "            font-size: 16px;\n" +
                "            line-height: 1.5;\n" +
                "            margin-bottom: 15px;\n" +
                "        }\n" +
                "\n" +
                "        .verification-link {\n" +
                "            display: inline-block;\n" +
                "            padding: 10px 20px;\n" +
                "            background-color: #342929;\n" +
                "            color: #fff !important;\n" +
                "            text-decoration: none;\n" +
                "            border-radius: 5px;\n" +
                "        }\n" +
                "\n" +
                "        .verification-link:hover {\n" +
                "            background-color: #2fa64d;\n" +
                "        }\n" +
                "\n" +
                "        .footer {\n" +
                "            text-align: center;\n" +
                "            padding: 10px;\n" +
                "            background-color: #f2f2f2;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "\n" +
                "<body>\n" +
                "    <div class=\"container\">\n" +
                "        <div class=\"header\">\n" +
                "            <h1>Hotel Roxfort</h1>\n" +
                "        </div>\n" +
                "        <div class=\"content\">\n" +
                "            <p class=\"welcome-text\">Dear " + " <strong> " + command.getFirstName() + " " + command.getLastName() + "</strong>,</p>\n" +
                "            <p class=\"welcome-text\">Welcome to Hotel Roxfort! <br>We are thrilled to have you as our guest. Get ready for an enchanting experience filled with comfort and hospitality.</p>\n" +
                "            <p class=\"welcome-text\">Your adventure begins now! To complete your registration, please click the verification link below:</p>\n" +
                "            <a href=\"" + verificationLink + "\" class=\"verification-link\">Verify your registration</a>\n" +
                "            <p class=\"welcome-text\">If you have any questions, feel free to contact our magical concierge.</p>\n" +
                "            <p class=\"welcome-text\">Wishing you a spellbinding stay at Hotel Roxfort!</p>\n" +
                "        </div>\n" +
                "        <div class=\"footer\">\n" +
                "            &copy; 2024 Hotel Roxfort. All rights reserved.\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</body>\n" +
                "\n" +
                "</html>";

        appUserRepository.save(appUser);
        emailService.sendEmail(command.getEmail(), " Registration ", messageEmail);

        return modelMapper.map(appUser, AppUserInfo.class);
    }

    public AppUser findUserById(Long id) {
        Optional<AppUser> optionalAppUser = appUserRepository.findById(id);
        if (optionalAppUser.isEmpty()) {
            throw new UserNotFoundException(id);
        }
        return optionalAppUser.get();
    }

    public AppUserInfo getUserById(Long id) {
        AppUser appUser = findUserById(id);
        return modelMapper.map(appUser, AppUserInfo.class);
    }

    public AppUserInfo updateAppUserByIdAsUser(Long id, AppUserUpdateCommand command) throws MessagingException, UnsupportedEncodingException {
        AppUser appUser = findUserById(id);


        modelMapper.map(command, appUser);
        appUser.setUpdatePending(true);

        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(1);

        String verificationToken = UUID.randomUUID().toString();
        appUser.setVerificationToken(verificationToken);
        appUser.setVerificationTokenExpiration(expirationTime);

        String verificationLink = "http://localhost:8080/api/users/verifyUpdate?token=" + verificationToken;

        String messageEmail = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Hotel Roxfort - Welcome!</title>\n" +
                "    <style>\n" +
                "        body {\n" +
                "            font-family: 'Arial', sans-serif;\n" +
                "            background-color: #f2f2f2;\n" +
                "            margin: 0;\n" +
                "            padding: 0;\n" +
                "        }\n" +
                "\n" +
                "        .container {\n" +
                "            max-width: 600px;\n" +
                "            margin: 20px auto;\n" +
                "            background-color: #fff;\n" +
                "            border-radius: 8px;\n" +
                "            overflow: hidden;\n" +
                "        }\n" +
                "\n" +
                "        .header {\n" +
                "            background-color: #342929;\n" +
                "            color: #fff;\n" +
                "            text-align: center;\n" +
                "            padding: 20px;\n" +
                "        }\n" +
                "\n" +
                "        .logo {\n" +
                "            max-width: 100px;\n" +
                "            height: auto;\n" +
                "        }\n" +
                "\n" +
                "        h1 {\n" +
                "            margin: 0;\n" +
                "            font-size: 24px;\n" +
                "        }\n" +
                "\n" +
                "        .content {\n" +
                "            padding: 20px;\n" +
                "        }\n" +
                "\n" +
                "        .welcome-text {\n" +
                "            font-size: 16px;\n" +
                "            line-height: 1.5;\n" +
                "            margin-bottom: 15px;\n" +
                "        }\n" +
                "\n" +
                "        .verification-link {\n" +
                "            display: inline-block;\n" +
                "            padding: 10px 20px;\n" +
                "            background-color: #342929;\n" +
                "            color: #fff !important;\n" +
                "            text-decoration: none;\n" +
                "            border-radius: 5px;\n" +
                "        }\n" +
                "\n" +
                "        .verification-link:hover {\n" +
                "            background-color: #2fa64d;\n" +
                "        }\n" +
                "\n" +
                "        .footer {\n" +
                "            text-align: center;\n" +
                "            padding: 10px;\n" +
                "            background-color: #f2f2f2;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "\n" +
                "<body>\n" +
                "    <div class=\"container\">\n" +
                "        <div class=\"header\">\n" +
                "            <h1>Hotel Roxfort</h1>\n" +
                "        </div>\n" +
                "        <div class=\"content\">\n" +
                "            <p class=\"welcome-text\">Dear " + " <strong> " + command.getFirstName() + " " + command.getLastName() + "</strong>,</p>\n" +
                "            <p class=\"welcome-text\">Welcome to Hotel Roxfort! <br>We are thrilled to have you as our guest.</p>\n" +
                "            <p class=\"welcome-text\">To finalize your account update, please click the verification link below:</p>\n" +
                "            <a href=\"" + verificationLink + "\" class=\"verification-link\">Verify your account update</a>\n" +
                "            <p class=\"welcome-text\">If you have any questions, feel free to contact our magical concierge.</p>\n" +
                "            <p class=\"welcome-text\">Wishing you a spellbinding stay at Hotel Roxfort!</p>\n" +
                "        </div>\n" +
                "        <div class=\"footer\">\n" +
                "            &copy; 2024 Hotel Roxfort. All rights reserved.\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</body>\n" +
                "\n" +
                "</html>";

        emailService.sendEmail(command.getEmail(), " User update Verification ", messageEmail);
        appUserRepository.save(appUser);


        return modelMapper.map(appUser, AppUserInfo.class);
    }

    public AppUserInfo updateAppUserByIdAsAdmin(Long id, AppUserAdminUpdateCommand command) {
        AppUser appUser = findUserById(id);
        modelMapper.map(command, appUser);
        return modelMapper.map(appUser, AppUserInfo.class);
    }

    public List<AppUserListInfo> findAllAppUsers(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<AppUser> appUserPage = appUserRepository.findAll(pageable);

        List<AppUserListInfo> appUsers = appUserPage.getContent()
                .stream()
                .map(appUser -> modelMapper.map(appUser, AppUserListInfo.class))
                .collect(Collectors.toList());

        return appUsers;
    }

    public void deleteAppUser(Long id) throws MessagingException, UnsupportedEncodingException {
        AppUser appUser = findUserById(id);

        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(1);

        String verificationToken = UUID.randomUUID().toString();
        appUser.setVerificationToken(verificationToken);
        appUser.setVerificationTokenExpiration(expirationTime);

        String verificationLink = "http://localhost:8080/api/users/verifyDelete?token=" + verificationToken;

        String messageEmail = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Hotel Roxfort - Welcome!</title>\n" +
                "    <style>\n" +
                "        body {\n" +
                "            font-family: 'Arial', sans-serif;\n" +
                "            background-color: #f2f2f2;\n" +
                "            margin: 0;\n" +
                "            padding: 0;\n" +
                "        }\n" +
                "\n" +
                "        .container {\n" +
                "            max-width: 600px;\n" +
                "            margin: 20px auto;\n" +
                "            background-color: #fff;\n" +
                "            border-radius: 8px;\n" +
                "            overflow: hidden;\n" +
                "        }\n" +
                "\n" +
                "        .header {\n" +
                "            background-color: #342929;\n" +
                "            color: #fff;\n" +
                "            text-align: center;\n" +
                "            padding: 20px;\n" +
                "        }\n" +
                "\n" +
                "        .logo {\n" +
                "            max-width: 100px;\n" +
                "            height: auto;\n" +
                "        }\n" +
                "\n" +
                "        h1 {\n" +
                "            margin: 0;\n" +
                "            font-size: 24px;\n" +
                "        }\n" +
                "\n" +
                "        .content {\n" +
                "            padding: 20px;\n" +
                "        }\n" +
                "\n" +
                "        .welcome-text {\n" +
                "            font-size: 16px;\n" +
                "            line-height: 1.5;\n" +
                "            margin-bottom: 15px;\n" +
                "        }\n" +
                "\n" +
                "        .verification-link {\n" +
                "            display: inline-block;\n" +
                "            padding: 10px 20px;\n" +
                "            background-color: #342929;\n" +
                "            color: #fff !important;\n" +
                "            text-decoration: none;\n" +
                "            border-radius: 5px;\n" +
                "        }\n" +
                "\n" +
                "        .verification-link:hover {\n" +
                "            background-color: #2fa64d;\n" +
                "        }\n" +
                "\n" +
                "        .footer {\n" +
                "            text-align: center;\n" +
                "            padding: 10px;\n" +
                "            background-color: #f2f2f2;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "\n" +
                "<body>\n" +
                "    <div class=\"container\">\n" +
                "        <div class=\"header\">\n" +
                "            <h1>Hotel Roxfort</h1>\n" +
                "        </div>\n" +
                "        <div class=\"content\">\n" +
                "            <p class=\"welcome-text\">Dear " + " <strong> " + appUser.getFirstName() + " " + appUser.getLastName() + "</strong>,</p>\n" +
                "            <p class=\"welcome-text\">Welcome to Hotel Roxfort! <br>We sadly hear about your account deactivation.</p>\n" +
                "            <p class=\"welcome-text\">If it was a mistake, please click the verification link below:</p>\n" +
                "            <a href=\"" + verificationLink + "\" class=\"verification-link\">Verify account reactivation </a>\n" +
                "            <p class=\"welcome-text\">If you have any questions, feel free to contact our magical concierge.</p>\n" +
                "            <p class=\"welcome-text\">Wishing you a spellbinding stay at Hotel Roxfort!</p>\n" +
                "        </div>\n" +
                "        <div class=\"footer\">\n" +
                "            &copy; 2024 Hotel Roxfort. All rights reserved.\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</body>\n" +
                "\n" +
                "</html>";
        emailService.sendEmail(appUser.getEmail(), "User reactivation Verification", messageEmail);


    }

//    public AuthenticationResponseDto login(AuthenticationRequestDto authenticationRequestDto) {
//        System.out.println("szervice login");
//
//
//        UsernamePasswordAuthenticationToken authenticationToken =
//                new UsernamePasswordAuthenticationToken(authenticationRequestDto.getEmail(), authenticationRequestDto.getPassword());
//
////        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        System.out.println("513-as sorban vagyok");
//        Authentication authentication = authenticationManager.authenticate(authenticationToken);
//        System.out.println("authenticationManager");
//        User user = (User) authentication.getPrincipal();
//        Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY.getBytes());
//        String accessToken = JWT.create()
//                // Unique data about user
//                .withSubject(user.getUsername())
//                .withExpiresAt(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION_TIME))
//                // Company name or company url
//                .withIssuer("http://localhost:8080/api/users/login")
//                // Roles
//                .withClaim("roles", user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
//                .sign(algorithm);
//        String refreshToken = JWT.create()
//                // Unique data about user
//                .withSubject(user.getUsername())
//                // Hosszabb idő kell, bármennyi lehet
//                .withExpiresAt(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION_TIME))
//                // Company name or company url
//                .withIssuer("http://localhost:8080/api/users/login")
//                .sign(algorithm);
//        return new AuthenticationResponseDto(accessToken, refreshToken, createAuthenticatedUserInfo(authenticationRequestDto));
//    }
//
//    private AuthenticatedUserInfo createAuthenticatedUserInfo(AuthenticationRequestDto authenticationRequestDto) {
//        AuthenticatedUserInfo authenticatedUserInfoJWT = new AuthenticatedUserInfo();
//        authenticatedUserInfoJWT.setEmail(authenticationRequestDto.getEmail());
//        authenticatedUserInfoJWT.setRoles(parseUserRoles(loadUserByUsername(authenticationRequestDto.getEmail())));
//        return authenticatedUserInfoJWT;
//    }
//
//    private List<UserRole> parseUserRoles(UserDetails user) {
//        return user.getAuthorities()
//                .stream()
//                .map(authority -> UserRole.valueOf(authority.getAuthority()))
//                .collect(Collectors.toList());
//    }
//
//    public RefreshTokenResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
//        String refreshToken = refreshTokenRequest.getRefreshToken();
//        Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY.getBytes());
//        JWTVerifier verifier = JWT.require(algorithm).build();
//        DecodedJWT decodedJWT = verifier.verify(refreshToken);
//        String username = decodedJWT.getSubject();
//        AppUser user = findByUsername(username);
//
//        String accessToken = JWT.create()
//                .withSubject(user.getEmail())
//                .withExpiresAt(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION_TIME))
//                .withIssuer("http://localhost:8080/api/users/token/refresh")
//                .withClaim("roles", user.getRoles().stream().map(UserRole::getRole).collect(Collectors.toList()))
//                .sign(algorithm);
//
//        return new RefreshTokenResponse(accessToken);
//    }

//    public AppUser findByUsername(String username) {
//        Optional<AppUser> appUserOptional = appUserRepository.findByEmail(username);
//        if (appUserOptional.isEmpty()) {
//            throw new UsernameNotFoundException(username);
//        }
//        return appUserOptional.get();
//    }



    public void saveAppUserToRoom(Long id, BookingCreateCommand command) throws MessagingException, UnsupportedEncodingException {
        AppUser appUser = findUserById(id);
        Room room = roomService.findRoomByRoomId(command.getRoomId());

        LocalDateTime inDate = command.getInDate();
        LocalDateTime outDate = command.getOutDate();

        if (bookingRoomUserService.isRoomAvailable(command.getRoomId(), inDate, outDate)) {
            BookingRoomUser bookingRoomUserServiceToSave = new BookingRoomUser();
            bookingRoomUserServiceToSave.setAppUser(appUser);
            bookingRoomUserServiceToSave.setRoom(room);
            bookingRoomUserServiceToSave.setInDate(command.getInDate());
            bookingRoomUserServiceToSave.setOutDate(command.getOutDate());

            LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(1);

            String verificationToken = UUID.randomUUID().toString();
            bookingRoomUserServiceToSave.setVerificationToken(verificationToken);
            bookingRoomUserServiceToSave.setVerificationTokenExpiration(expirationTime);

            String verificationLink = "http://localhost:8080/api/users/verifyBooking?token=" + verificationToken;

            String messageEmail = "<!DOCTYPE html>\n" +
                    "<html lang=\"en\">\n" +
                    "\n" +
                    "<head>\n" +
                    "    <meta charset=\"UTF-8\">\n" +
                    "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                    "    <title>Hotel Roxfort - Welcome!</title>\n" +
                    "    <style>\n" +
                    "        body {\n" +
                    "            font-family: 'Arial', sans-serif;\n" +
                    "            background-color: #f2f2f2;\n" +
                    "            margin: 0;\n" +
                    "            padding: 0;\n" +
                    "        }\n" +
                    "\n" +
                    "        .container {\n" +
                    "            max-width: 600px;\n" +
                    "            margin: 20px auto;\n" +
                    "            background-color: #fff;\n" +
                    "            border-radius: 8px;\n" +
                    "            overflow: hidden;\n" +
                    "        }\n" +
                    "\n" +
                    "        .header {\n" +
                    "            background-color: #342929;\n" +
                    "            color: #fff;\n" +
                    "            text-align: center;\n" +
                    "            padding: 20px;\n" +
                    "        }\n" +
                    "\n" +
                    "        .logo {\n" +
                    "            max-width: 100px;\n" +
                    "            height: auto;\n" +
                    "        }\n" +
                    "\n" +
                    "        h1 {\n" +
                    "            margin: 0;\n" +
                    "            font-size: 24px;\n" +
                    "        }\n" +
                    "\n" +
                    "        .content {\n" +
                    "            padding: 20px;\n" +
                    "        }\n" +
                    "\n" +
                    "        .welcome-text {\n" +
                    "            font-size: 16px;\n" +
                    "            line-height: 1.5;\n" +
                    "            margin-bottom: 15px;\n" +
                    "        }\n" +
                    "\n" +
                    "        .verification-link {\n" +
                    "            display: inline-block;\n" +
                    "            padding: 10px 20px;\n" +
                    "            background-color: #342929;\n" +
                    "            color: #fff !important;\n" +
                    "            text-decoration: none;\n" +
                    "            border-radius: 5px;\n" +
                    "        }\n" +
                    "\n" +
                    "        .verification-link:hover {\n" +
                    "            background-color: #2fa64d;\n" +
                    "        }\n" +
                    "\n" +
                    "        .footer {\n" +
                    "            text-align: center;\n" +
                    "            padding: 10px;\n" +
                    "            background-color: #f2f2f2;\n" +
                    "        }\n" +
                    "    </style>\n" +
                    "</head>\n" +
                    "\n" +
                    "<body>\n" +
                    "    <div class=\"container\">\n" +
                    "        <div class=\"header\">\n" +
                    "            <h1>Hotel Roxfort</h1>\n" +
                    "        </div>\n" +
                    "        <div class=\"content\">\n" +
                    "            <p class=\"welcome-text\">Dear " + " <strong> " + appUser.getFirstName() + " " + appUser.getLastName() + "</strong>,</p>\n" +
                    "            <p class=\"welcome-text\">Welcome to Hotel Roxfort! <br>We are thrilled to have you as our guest.</p>\n" +
                    "            <p class=\"welcome-text\">Reservation details:</p> " +
                    "            <p class=\"welcome-text\">House name: " + bookingRoomUserServiceToSave.getRoom().getHouse().getHouseName() + " </p>" +
                    "            <p class=\"welcome-text\">Room number: " + bookingRoomUserServiceToSave.getRoom().getRoomNumber() + " </p>" +
                    "            <p class=\"welcome-text\">Start date: " + bookingRoomUserServiceToSave.getInDate() + " </p>" +
                    "            <p class=\"welcome-text\">End date: " + bookingRoomUserServiceToSave.getOutDate() + " </p>\n" +
                    "            <p class=\"welcome-text\">To finalize your booking, please click the verification link below:</p>\n" +
                    "            <a href=\"" + verificationLink + "\" class=\"verification-link\">Verify your booking</a>\n" +
                    "            <p class=\"welcome-text\">If you have any questions, feel free to contact our magical concierge.</p>\n" +
                    "            <p class=\"welcome-text\">Wishing you a spellbinding stay at Hotel Roxfort!</p>\n" +
                    "        </div>\n" +
                    "        <div class=\"footer\">\n" +
                    "            &copy; 2024 Hotel Roxfort. All rights reserved.\n" +
                    "        </div>\n" +
                    "    </div>\n" +
                    "</body>\n" +
                    "\n" +
                    "</html>";
            emailService.sendEmail(appUser.getEmail(), "Booking Verification", messageEmail);
            bookingRoomUserService.saveBooking(bookingRoomUserServiceToSave);

        } else {
            throw new RoomNotAvailableException(command.getRoomId(), inDate, outDate);
        }

    }

    public BookingUpdateInfo updateBooking(Long id, BookingUpdateCommand command) throws MessagingException, UnsupportedEncodingException {
        BookingRoomUser bookingRoomUserToSave = bookingRoomUserService.findBookingById(id);
        Room room = roomService.findRoomByRoomId(command.getUpdateRoomId());
        bookingRoomUserToSave.setRoom(room);
        bookingRoomUserToSave.setInDate(command.getInDate());
        bookingRoomUserToSave.setOutDate(command.getOutDate());

        BookingUpdateInfo bookingUpdateInfo = new BookingUpdateInfo();
        bookingUpdateInfo.setUpdateRoomId(command.getUpdateRoomId());
        bookingUpdateInfo.setInDate(command.getInDate());
        bookingUpdateInfo.setOutDate(command.getOutDate());
        bookingRoomUserToSave.setUpdatePending(true);
        bookingRoomUserService.saveBooking(bookingRoomUserToSave);
        return bookingUpdateInfo;
    }

    public List<BookingInfo> findBookingByAppUserId(Long id) {

        List<BookingRoomUser> bookingRoomUserList = bookingRoomUserService.findBookingByUserId(id);

        return bookingRoomUserList.stream()
                .map(bookingRoomUser -> {
                    BookingInfo bookingInfo = new BookingInfo();
                    bookingInfo.setAppUserId(bookingRoomUser.getAppUser().getUserId());
                    bookingInfo.setRoomId(bookingRoomUser.getRoom().getRoomId());
                    bookingInfo.setInDate(bookingRoomUser.getInDate());
                    bookingInfo.setOutDate(bookingRoomUser.getOutDate());
                    return bookingInfo;
                })
                .collect(Collectors.toList());
    }

    public void deleteBooking(Long id) throws MessagingException, UnsupportedEncodingException {
        BookingRoomUser bookingRoomUser = bookingRoomUserService.findBookingByRoomId(id);
        bookingRoomUser.setDeleted(true);
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(1);

        String verificationToken = UUID.randomUUID().toString();
        bookingRoomUser.setVerificationToken(verificationToken);
        bookingRoomUser.setVerificationTokenExpiration(expirationTime);

        String verificationLink = "http://localhost:8080/api/users/deleteBooking?token=" + verificationToken;

        String messageEmail = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Hotel Roxfort - Welcome!</title>\n" +
                "    <style>\n" +
                "        body {\n" +
                "            font-family: 'Arial', sans-serif;\n" +
                "            background-color: #f2f2f2;\n" +
                "            margin: 0;\n" +
                "            padding: 0;\n" +
                "        }\n" +
                "\n" +
                "        .container {\n" +
                "            max-width: 600px;\n" +
                "            margin: 20px auto;\n" +
                "            background-color: #fff;\n" +
                "            border-radius: 8px;\n" +
                "            overflow: hidden;\n" +
                "        }\n" +
                "\n" +
                "        .header {\n" +
                "            background-color: #342929;\n" +
                "            color: #fff;\n" +
                "            text-align: center;\n" +
                "            padding: 20px;\n" +
                "        }\n" +
                "\n" +
                "        .logo {\n" +
                "            max-width: 100px;\n" +
                "            height: auto;\n" +
                "        }\n" +
                "\n" +
                "        h1 {\n" +
                "            margin: 0;\n" +
                "            font-size: 24px;\n" +
                "        }\n" +
                "\n" +
                "        .content {\n" +
                "            padding: 20px;\n" +
                "        }\n" +
                "\n" +
                "        .welcome-text {\n" +
                "            font-size: 16px;\n" +
                "            line-height: 1.5;\n" +
                "            margin-bottom: 15px;\n" +
                "        }\n" +
                "\n" +
                "        .verification-link {\n" +
                "            display: inline-block;\n" +
                "            padding: 10px 20px;\n" +
                "            background-color: #342929;\n" +
                "            color: #fff !important;\n" +
                "            text-decoration: none;\n" +
                "            border-radius: 5px;\n" +
                "        }\n" +
                "\n" +
                "        .verification-link:hover {\n" +
                "            background-color: #2fa64d;\n" +
                "        }\n" +
                "\n" +
                "        .footer {\n" +
                "            text-align: center;\n" +
                "            padding: 10px;\n" +
                "            background-color: #f2f2f2;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "\n" +
                "<body>\n" +
                "    <div class=\"container\">\n" +
                "        <div class=\"header\">\n" +
                "            <h1>Hotel Roxfort</h1>\n" +
                "        </div>\n" +
                "        <div class=\"content\">\n" +
                "            <p class=\"welcome-text\">Dear " + " <strong> " + bookingRoomUser.getAppUser().getFirstName() + " " + bookingRoomUser.getAppUser().getLastName() + "</strong>,</p>\n" +
                "            <p class=\"welcome-text\">Welcome to Hotel Roxfort! <br>We sadly hear about your booking deletion.</p>\n" +
                "            <p class=\"welcome-text\">If it was a mistake and you would like to keep your booking, please click the verification link below:</p>\n" +
                "            <a href=\"" + verificationLink + "\" class=\"verification-link\">Keep my booking</a>\n" +
                "            <p class=\"welcome-text\">If you have any questions, feel free to contact our magical concierge.</p>\n" +
                "            <p class=\"welcome-text\">Wishing you a spellbinding day!</p>\n" +
                "        </div>\n" +
                "        <div class=\"footer\">\n" +
                "            &copy; 2024 Hotel Roxfort. All rights reserved.\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</body>\n" +
                "\n" +
                "</html>";

        emailService.sendEmail(bookingRoomUser.getAppUser().getEmail(), "Booking undelete Verification", messageEmail);
    }

    public boolean verifyUserByToken(String token) {
        AppUser appUser = (AppUser) appUserRepository.findByVerificationToken(token)
                .orElseThrow(() -> new TokenNotFoundException());

        if (appUser.getVerificationTokenExpiration() != null
                && LocalDateTime.now().isBefore(appUser.getVerificationTokenExpiration())) {

            appUser.setVerified(true);
            appUser.setVerificationToken(null);
            appUserRepository.save(appUser);
            return true;
        } else {
            throw new TokenNotFoundException();
        }
    }

    public boolean verifyBookingByToken(String token) {

        BookingRoomUser bookingRoomUser = bookingRoomUserService.findByVerificationToken(token)
                .orElseThrow(() -> new TokenNotFoundException());
        if (bookingRoomUser.getVerificationTokenExpiration() != null
                && LocalDateTime.now().isBefore(bookingRoomUser.getVerificationTokenExpiration())) {

            bookingRoomUser.setVerified(true);
            bookingRoomUser.setVerificationToken(null);
            bookingRoomUserService.save(bookingRoomUser);

            return true;
        } else {
            throw new TokenNotFoundException();
        }
    }

    public boolean undeleteBookingByToken(String token) {

        BookingRoomUser bookingRoomUser = bookingRoomUserService.findByVerificationToken(token)
                .orElseThrow(() -> new TokenNotFoundException());

        if (bookingRoomUser.getVerificationTokenExpiration() != null
                && LocalDateTime.now().isBefore(bookingRoomUser.getVerificationTokenExpiration())) {

            bookingRoomUser.setDeleted(false);
            bookingRoomUser.setVerificationToken(null);
            bookingRoomUserService.save(bookingRoomUser);
            return true;
        } else {

            throw new TokenNotFoundException();
        }
    }

    public boolean verifyUpdateByToken(String token) {

        AppUser appUser = (AppUser) appUserRepository.findByVerificationToken(token)
                .orElseThrow(() -> new TokenNotFoundException());

        if (appUser.getVerificationTokenExpiration() != null
                && LocalDateTime.now().isBefore(appUser.getVerificationTokenExpiration())) {

            appUser.setUpdatePending(false);
            appUserRepository.save(appUser);
            return true;
        } else {
            throw new TokenNotFoundException();
        }
    }

    public boolean verifyDeleteByToken(String token) {

        AppUser appUser = (AppUser) appUserRepository.findByVerificationToken(token)
                .orElseThrow(() -> new TokenNotFoundException());

        if (appUser.getVerificationTokenExpiration() != null
                && LocalDateTime.now().isBefore(appUser.getVerificationTokenExpiration())) {

            appUser.setAppUserDeactivated(true);
            appUser.setVerificationToken(null);
            appUserRepository.save(appUser);
            return true;
        } else {
            throw new TokenNotFoundException();
        }
    }

    public List<AppUser> findUnverifiedUsers() {
        return appUserRepository.findByIsVerifiedFalseAndVerificationTokenExpirationBefore(LocalDateTime.now());
    }

    @Scheduled(fixedRate = 30000)
    public void deleteUnverifiedUsers() {
        List<AppUser> unverifiedUsers = findUnverifiedUsers();

        for (AppUser user : unverifiedUsers) {
            appUserRepository.delete(user);
        }
    }

    @Scheduled(fixedRate = 30000)
    public void remindUnverifiedUsersUpdate() throws MessagingException, UnsupportedEncodingException {
        List<AppUser> unverifiedUsers = findUnverifiedUsersUpdate();

        for (AppUser user : unverifiedUsers) {
            LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(1);
            String verificationToken = UUID.randomUUID().toString();
            user.setVerificationToken(verificationToken);
            user.setVerificationTokenExpiration(expirationTime);

            String verificationLink = "http://localhost:8080/api/users/verifyUpdate?token=" + verificationToken;
            String messageEmail = "<!DOCTYPE html>\n" +
                    "<html lang=\"en\">\n" +
                    "\n" +
                    "<head>\n" +
                    "    <meta charset=\"UTF-8\">\n" +
                    "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                    "    <title>Hotel Roxfort - Welcome!</title>\n" +
                    "    <style>\n" +
                    "        body {\n" +
                    "            font-family: 'Arial', sans-serif;\n" +
                    "            background-color: #f2f2f2;\n" +
                    "            margin: 0;\n" +
                    "            padding: 0;\n" +
                    "        }\n" +
                    "\n" +
                    "        .container {\n" +
                    "            max-width: 600px;\n" +
                    "            margin: 20px auto;\n" +
                    "            background-color: #fff;\n" +
                    "            border-radius: 8px;\n" +
                    "            overflow: hidden;\n" +
                    "        }\n" +
                    "\n" +
                    "        .header {\n" +
                    "            background-color: #342929;\n" +
                    "            color: #fff;\n" +
                    "            text-align: center;\n" +
                    "            padding: 20px;\n" +
                    "        }\n" +
                    "\n" +
                    "        .logo {\n" +
                    "            max-width: 100px;\n" +
                    "            height: auto;\n" +
                    "        }\n" +
                    "\n" +
                    "        h1 {\n" +
                    "            margin: 0;\n" +
                    "            font-size: 24px;\n" +
                    "        }\n" +
                    "\n" +
                    "        .content {\n" +
                    "            padding: 20px;\n" +
                    "        }\n" +
                    "\n" +
                    "        .welcome-text {\n" +
                    "            font-size: 16px;\n" +
                    "            line-height: 1.5;\n" +
                    "            margin-bottom: 15px;\n" +
                    "        }\n" +
                    "\n" +
                    "        .verification-link {\n" +
                    "            display: inline-block;\n" +
                    "            padding: 10px 20px;\n" +
                    "            background-color: #342929;\n" +
                    "            color: #fff !important;\n" +
                    "            text-decoration: none;\n" +
                    "            border-radius: 5px;\n" +
                    "        }\n" +
                    "\n" +
                    "        .verification-link:hover {\n" +
                    "            background-color: #2fa64d;\n" +
                    "        }\n" +
                    "\n" +
                    "        .footer {\n" +
                    "            text-align: center;\n" +
                    "            padding: 10px;\n" +
                    "            background-color: #f2f2f2;\n" +
                    "        }\n" +
                    "    </style>\n" +
                    "</head>\n" +
                    "\n" +
                    "<body>\n" +
                    "    <div class=\"container\">\n" +
                    "        <div class=\"header\">\n" +
                    "            <h1>Hotel Roxfort</h1>\n" +
                    "        </div>\n" +
                    "        <div class=\"content\">\n" +
                    "            <p class=\"welcome-text\">Dear " + " <strong> " + user.getFirstName() + " " + user.getLastName() + "</strong>,</p>\n" +
                    "            <p class=\"welcome-text\">Welcome to Hotel Roxfort! <br>We are thrilled to have you as our guest.</p>\n" +
                    "            <p class=\"welcome-text\">To finalize your account update, please click the verification link below:</p>\n" +
                    "            <a href=\"" + verificationLink + "\" class=\"verification-link\">Verify your account update</a>\n" +
                    "            <p class=\"welcome-text\">If you have any questions, feel free to contact our magical concierge.</p>\n" +
                    "            <p class=\"welcome-text\">Wishing you a spellbinding stay at Hotel Roxfort!</p>\n" +
                    "        </div>\n" +
                    "        <div class=\"footer\">\n" +
                    "            &copy; 2024 Hotel Roxfort. All rights reserved.\n" +
                    "        </div>\n" +
                    "    </div>\n" +
                    "</body>\n" +
                    "\n" +
                    "</html>";

            emailService.sendEmail(user.getEmail(), " User update Verification ", messageEmail);
        }
    }

    private List<AppUser> findUnverifiedUsersUpdate() {
        return appUserRepository.findByIsUpdatePendingTrueAndVerificationTokenExpirationBefore(LocalDateTime.now());
    }

    public void updateBookingWithExtras(Long bookingId, BookingExtraUpdateCommand command) {
        BookingRoomUser bookingRoomUserToUpdate = bookingRoomUserService.findBookingById(bookingId);

        List<BookingExtra> bookingExtraList = new ArrayList<>(bookingRoomUserToUpdate.getBookingExtraList());
        Extra extra = extraService.findExtraById(command.getExtraId());
        BookingExtra bookingExtra = new BookingExtra();
        bookingExtraService.saveBookingExtra(bookingExtra);
        bookingExtra.setBookingRoomUser(bookingRoomUserToUpdate);
        bookingExtra.setExtra(extra);
        bookingExtra.setQuantity(command.getQuantity());
        bookingExtraList.add(bookingExtra);
        bookingRoomUserToUpdate.setBookingExtraList(bookingExtraList);
        bookingRoomUserService.saveBooking(bookingRoomUserToUpdate);
    }

    public void sendReminderMailById(Long id) throws MessagingException, UnsupportedEncodingException {

        BookingRoomUser bookingRoomUser = bookingRoomUserService.findBookingById(id);

        double degreeC = weatherService.getWeatherData("Budapest").getTemp();
        String funnyMessage = null;
        if (degreeC <= 0) {
            funnyMessage = "It's going to be chill, so don't forget to bring your fluffy coat!";
        } else if (degreeC > 0 && degreeC <= 20) {
            funnyMessage = "It's going to be cool, so don't forget to bring your long sleeves!";
        } else if (degreeC > 20) {
            funnyMessage = "It's going to be hot, so don't forget to bring your sunglasses!";
        }

        String messageEmail = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Hotel Roxfort - Welcome!</title>\n" +
                "    <style>\n" +
                "        body {\n" +
                "            font-family: 'Arial', sans-serif;\n" +
                "            background-color: #f2f2f2;\n" +
                "            margin: 0;\n" +
                "            padding: 0;\n" +
                "        }\n" +
                "\n" +
                "        .container {\n" +
                "            max-width: 600px;\n" +
                "            margin: 20px auto;\n" +
                "            background-color: #fff;\n" +
                "            border-radius: 8px;\n" +
                "            overflow: hidden;\n" +
                "        }\n" +
                "\n" +
                "        .header {\n" +
                "            background-color: #342929;\n" +
                "            color: #fff;\n" +
                "            text-align: center;\n" +
                "            padding: 20px;\n" +
                "        }\n" +
                "\n" +
                "        .logo {\n" +
                "            max-width: 100px;\n" +
                "            height: auto;\n" +
                "        }\n" +
                "\n" +
                "        h1 {\n" +
                "            margin: 0;\n" +
                "            font-size: 24px;\n" +
                "        }\n" +
                "\n" +
                "        .content {\n" +
                "            padding: 20px;\n" +
                "        }\n" +
                "\n" +
                "        .welcome-text {\n" +
                "            font-size: 16px;\n" +
                "            line-height: 1.5;\n" +
                "            margin-bottom: 15px;\n" +
                "        }\n" +
                "\n" +
                "        .verification-link {\n" +
                "            display: inline-block;\n" +
                "            padding: 10px 20px;\n" +
                "            background-color: #342929;\n" +
                "            color: #fff !important;\n" +
                "            text-decoration: none;\n" +
                "            border-radius: 5px;\n" +
                "        }\n" +
                "\n" +
                "        .verification-link:hover {\n" +
                "            background-color: #2fa64d;\n" +
                "        }\n" +
                "\n" +
                "        .footer {\n" +
                "            text-align: center;\n" +
                "            padding: 10px;\n" +
                "            background-color: #f2f2f2;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "\n" +
                "<body>\n" +
                "    <div class=\"container\">\n" +
                "        <div class=\"header\">\n" +
                "            <h1>Hotel Roxfort</h1>\n" +
                "        </div>\n" +
                "        <div class=\"content\">\n" +
                "            <p class=\"welcome-text\">Dear " + " <strong> " + bookingRoomUser.getAppUser().getFirstName() + " " + bookingRoomUser.getAppUser().getLastName() + "</strong>,</p>\n" +
                "            <p class=\"welcome-text\">Welcome to Hotel Roxfort! <br>We are thrilled to have you as our guest. Get ready for an enchanting experience filled with comfort and hospitality.</p>\n" +
                "            <p class=\"welcome-text\">Here are the current weather details for your arrival date at<br>Budapest around 3pm on " + bookingRoomUser.getInDate().toLocalDate() + ": </p>\n" +
                "            <p class=\"weather-data\" style=\"margin-bottom: 0;\">Temperature: <span style=\"font-weight: bold;\">" + degreeC + " C°</span></p>\n" +
                "            <p class=\"weather-data\" style=\"margin-top: 0;\">Weather: <span style=\"font-weight: bold;\">" + weatherService.getWeatherData("Budapest").getDescription() + "<br> " + funnyMessage + "</span></p>\n" +
                "            <p class=\"welcome-text\">If you have any questions, feel free to contact our magical concierge.</p>\n" +
                "            <p class=\"welcome-text\">Wishing you a spellbinding stay at Hotel Roxfort!</p>\n" +
                "        </div>\n" +
                "        <div class=\"footer\">\n" +
                "            &copy; 2024 Hotel Roxfort. All rights reserved.\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</body>\n" +
                "\n" +
                "</html>";


        emailService.sendEmail(bookingRoomUser.getAppUser().getEmail(), "Welcome reminder", messageEmail);
    }


    @Scheduled(cron = "0 0 8 * * ?")
    public void sendReminderMail() throws MessagingException, UnsupportedEncodingException {
        List<BookingRoomUser> bookingRoomUserList = bookingRoomUserService.findAllBookingsForEmail();

        LocalDate currentDate = LocalDate.now();

        for (BookingRoomUser bookingRoomUser : bookingRoomUserList) {
            long daysUntilArrival = ChronoUnit.DAYS.between(currentDate, bookingRoomUser.getInDate());

            if (daysUntilArrival == 3) {
                sendReminderMailById(bookingRoomUser.getBookingId());
            }
        }
    }

    public List<BookingExtra> listAllBookingExtrasByBookingId(Long bookingId) {
        List<BookingExtra> bookingExtraList = bookingExtraService.listAllBookingExtrasByBookingId(bookingId);
        return bookingExtraList;
    }

    public void sendBillInEmail(BookingRoomUser bookingRoomUser, List<BookingExtra> bookingExtraList) throws MessagingException, IOException {
        emailService.sendEmailWithAttachment(bookingRoomUser, bookingExtraList);
    }

    public InputStreamSource createBill(Long bookingId) throws IOException {
        BookingRoomUser bookingRoomUser = bookingRoomUserService.findBookingById(bookingId);
        List<BookingExtra> bookingExtraList = bookingExtraService.listAllBookingExtrasByBookingId(bookingId);
        return PdfGeneratorService.createBillPdf(bookingRoomUser, bookingExtraList, exchangeService);
    }
}


