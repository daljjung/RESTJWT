package com.example.demo.lectures;

import com.example.demo.common.ErrorsResource;
import com.example.demo.exception.BusinessException;
import com.example.demo.exception.advice.DefaultExceptionAdvice;
import com.example.demo.lectures.dto.LectureReqDto;
import com.example.demo.lectures.dto.LectureResDto;
import com.example.demo.userinfo.CurrentUser;
import com.example.demo.userinfo.UserInfo;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Slf4j
@RestController
//@RequestMapping(value="/api/lectures", produces = MediaTypes.HAL_JSON_VALUE)
@RequestMapping(value="/api/lectures")
public class LectureController {

    private final LectureRepository lectureRepository;
    private final ModelMapper modelMapper;
    private final LectureValidator lectureValidator;

    public LectureController(LectureRepository lectureRepository, ModelMapper modelMapper, LectureValidator lectureValidator) {
        this.lectureRepository= lectureRepository;
        this.modelMapper= modelMapper;
        this.lectureValidator= lectureValidator;
    }

    @PostMapping
    public ResponseEntity<?> createLecture(@RequestBody @Valid LectureReqDto lectureReqDto,
                                           Errors errors,
                                           @CurrentUser UserInfo currentUser) {
        //입력항목 검증
        if (errors.hasErrors()) {
            //status code 400
            //return ResponseEntity.badRequest().body(new ErrorsResource(errors));
            return getErrors(errors);
        }
        //biz로직과 관련된 입력항목 검증
        this.lectureValidator.validate(lectureReqDto, errors);
        if (errors.hasErrors()) {
            return getErrors(errors);
        }

        //ReqDto -> Entity
        Lecture lecture = modelMapper.map(lectureReqDto, Lecture.class);
        //offline, free 값을 update
        lecture.update();
        //Lecture와 UserInfo 연관관계 설정
        lecture.setUserInfo(currentUser);
        Lecture addLecture = this.lectureRepository.save(lecture);
        //Entity -> ResDto
        LectureResDto lectureResDto = modelMapper.map(addLecture, LectureResDto.class);
        //LectureResDto 에 UserInfo 객체의 email set
        lectureResDto.setEmail(addLecture.getUserInfo().getEmail());

        WebMvcLinkBuilder selfLinkBuilder =
                linkTo(LectureController.class).slash(lectureResDto.getId());
        URI createUri = selfLinkBuilder.toUri();

        LectureResource lectureResource = new LectureResource(lectureResDto);
        lectureResource.add(linkTo(LectureController.class).withRel("query-lectures"));
        lectureResource.add(selfLinkBuilder.withRel("update-lecture"));

        return ResponseEntity.created(createUri).body(lectureResource);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> queryLectures(Pageable pageable,
                                           PagedResourcesAssembler<LectureResDto> assembler,
                                           @CurrentUser UserInfo currentUser) {

        Page<Lecture> lecturePage = this.lectureRepository.findAll(pageable);

        Page<LectureResDto> lectureResDtoPage =
                lecturePage.map(lecture -> {
                    LectureResDto lectureResDto = new LectureResDto();
                    if (lecture.getUserInfo() != null) {
                        lectureResDto.setEmail(lecture.getUserInfo().getEmail());
                    }
                    modelMapper.map(lecture, lectureResDto);
                    return lectureResDto;
                });

        PagedModel<LectureResource> pagedModel =
                //assembler.toModel(lectureResDtoPage, resDto -> new LectureResource(resDto));
                assembler.toModel(lectureResDtoPage, LectureResource::new);

        if (currentUser != null) {
            pagedModel.add(linkTo(LectureController.class).withRel("create-Lecture"));
        }

        return ResponseEntity.ok(pagedModel);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<?> getLecture(@PathVariable Integer id, @CurrentUser UserInfo currentUser) {
        Optional<Lecture> optionalLecture = this.lectureRepository.findById(id);
        if (optionalLecture.isEmpty()) {
            //return ResponseEntity.notFound().build();
            throw new BusinessException(id + " Lecture Not Found", HttpStatus.NOT_FOUND);
        }

        Lecture lecture = optionalLecture.get();
        LectureResDto lectureResDto = modelMapper.map(lecture, LectureResDto.class);
        if (lecture.getUserInfo() != null)
            lectureResDto.setEmail(lecture.getUserInfo().getEmail());

        LectureResource lectureResource = new LectureResource(lectureResDto);
        //인증토큰의 email과 Lecture가 참조하는 email주소가 같으면 update 링크를 제공하기
        if ((lecture.getUserInfo() != null) && (lecture.getUserInfo().equals(currentUser))) {
            lectureResource.add(linkTo(LectureController.class)
                    .slash(lecture.getId()).withRel("update-lecture"));
        }
        return ResponseEntity.ok(lectureResource);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateLecture(@PathVariable Integer id,
                                           @RequestBody @Valid LectureReqDto lectureReqDto,
                                           Errors errors,
                                           @CurrentUser UserInfo currentUser) {
        Optional<Lecture> optionalLecture = lectureRepository.findById(id);
        if (optionalLecture.isEmpty()) {
            throw new BusinessException(id + " Lecture Not Found", HttpStatus.NOT_FOUND);
        }

        if (errors.hasErrors()) {
            return getErrors(errors);
        }

        lectureValidator.validate(lectureReqDto, errors);
        if (errors.hasErrors()) {
            return getErrors(errors);
        }

        Lecture existingLecture = optionalLecture.get();
        //Lecture가 참조하는 UserInfo 객체와 인증한 UserInfo 객체가 다르면 401 인증 오류
        if((existingLecture.getUserInfo() != null) && (!existingLecture.getUserInfo().equals(currentUser))) {
            throw new BadCredentialsException("등록한 User와 수정을 요청한 User가 다릅니다.");
            //return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }
        //ReqDto -> Entity
        this.modelMapper.map(lectureReqDto, existingLecture);
        existingLecture.update();
        Lecture savedLecture = this.lectureRepository.save(existingLecture);
        //Entity -> ResDto
        LectureResDto lectureResDto = modelMapper.map(savedLecture, LectureResDto.class);

        //Lecture 객체와 연관된 UserInfo 객체가 있다면 LectureResDto에 email을 set
        if(savedLecture.getUserInfo() != null)
            lectureResDto.setEmail(savedLecture.getUserInfo().getEmail());
        //ResDto -> Resource
        LectureResource lectureResource = new LectureResource(lectureResDto);
        return ResponseEntity.ok(lectureResource);
    }

    private ResponseEntity<?> getErrors(Errors errors){
        return ResponseEntity.badRequest().body(new ErrorsResource(errors));
    }

}
