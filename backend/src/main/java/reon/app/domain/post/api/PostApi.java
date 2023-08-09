package reon.app.domain.post.api;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reon.app.domain.post.dto.req.PrivatePostUpdateRequest;
import reon.app.domain.post.dto.res.*;
import reon.app.domain.post.entity.Scope;
import reon.app.domain.post.service.PostLikeService;
import reon.app.domain.post.service.PostQueryService;
import reon.app.domain.post.service.PostService;
import reon.app.domain.post.service.dto.PostSaveDto;
import reon.app.domain.post.service.dto.PrivatePostUpdateDto;
import reon.app.global.api.ApiResponse;
import reon.app.global.error.entity.CustomException;
import reon.app.global.error.entity.ErrorCode;

import java.util.List;

@Api(tags = "Post")
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("api/post-management/post")
public class PostApi {
    private final PostService postService;
    private final PostQueryService postQueryService;

//    private final MemberService memberService;
    private final PostLikeService postLikeService;

    @Operation(summary = "post 저장", description = " 유저 연기 영상, 원본 영상ID를 입력받아 post를 저장")
    @PostMapping
    public ApiResponse<?> savePost(@RequestPart MultipartFile actionVideo, @RequestParam("videoId") Long videoId,
                                   @Parameter(hidden = true) @AuthenticationPrincipal User user){
        PostSaveDto postSaveDto = PostSaveDto.builder()
                .memberId(Long.parseLong(user.getUsername()))
                .actionVideo(actionVideo)
                .videoId(videoId)
                .build();
        postService.save(postSaveDto);

        return ApiResponse.OK(null);
    }

    // 단건 조회
    @Operation(summary = "post 상세 조회", description = "postId로 post 상세 조회")
    @GetMapping("/{postId}")
    public ApiResponse<?> searchPost(@PathVariable Long postId){
        Scope postScope = postQueryService.searchScopeById(postId);
        if(postScope == null){
            throw new CustomException(ErrorCode.POSTS_NOT_FOUND);
        }
        if(postScope.equals(Scope.PRIVATE)){ // title, content 제공 x
            PrivateDetailPostResponse response = postQueryService.searchPrivateById(postId);
            return ApiResponse.OK(response);

        }else{
            PublicDetailPostResponse response = postQueryService.searchPublicById(postId);
            return ApiResponse.OK(response);
        }
    }


    @Operation(summary = "mypage private post 목록 조회", description = "PRIVATE 게시글 목록을 조회한다.")
    @GetMapping("/private")
    public ApiResponse<?> searchPrivatePosts(@RequestParam(value = "offset") Long offset, @Parameter(hidden = true) @AuthenticationPrincipal User user ){
        Long memberId = Long.parseLong(user.getUsername());
        log.info(String.valueOf(memberId));
        List<PrivatePostsResponse> responses = postQueryService.searchPrivatePosts(offset, memberId);
        log.info(responses.toString());
        return ApiResponse.OK(responses);
    }


    @Operation(summary = "mypage 내가 좋아요 누른 post 목록 조회", description = "liked 게시글 목록을 조회한다.")
    @GetMapping("/liked")
    public ApiResponse<?> searchLikedPosts(@RequestParam(value = "offset") Long offset, @Parameter(hidden = true) @AuthenticationPrincipal User user ) {
        Long memberId = Long.parseLong(user.getUsername());
        List<PostsResponse> responses = postQueryService.searchLikedPosts(offset, memberId);
        return ApiResponse.OK(responses);
    }


    @Operation(summary = "mypage public post 목록 조회", description = "PUBLIC 게시글 목록을 조회한다.")
    @GetMapping("/public")
    public ApiResponse<?> searchPublicPosts(@RequestParam(value = "offset") Long offset, @RequestParam(value = "memberId") Long memberId){
        log.info(String.valueOf(memberId));
        List<PublicPostsResponse> responses = postQueryService.searchPublicPosts(offset, memberId);
        log.info(responses.toString());
        return ApiResponse.OK(responses);
    }

    @Operation(summary = "투표해줘 public post 목록 전체 조회", description = "투표해줘 PUBLIC 게시글 목록을 조회한다.")
    @GetMapping("/feed")
    public ApiResponse<?> searchFeedPosts(@RequestParam(value = "offset") Long offset){
        List<PostsResponse> responses = postQueryService.searchFeedPosts(offset);
        return ApiResponse.OK(responses);
    }


    @Operation(summary = "private post upload", description = "PRIVATE 게시글을 PUBLIC으로 변경한다.")
    @PutMapping("/private/{postId}")
    public ApiResponse<?> updatePrivatePost(@PathVariable Long postId, @RequestBody PrivatePostUpdateRequest request){
        Scope scope = postQueryService.searchScopeById(postId);
        if(scope.equals(Scope.PUBLIC)){
            throw new CustomException(ErrorCode.POST_SCOPE_ERROR);
        }
        PrivatePostUpdateDto dto = PrivatePostUpdateDto.builder()
                .id(postId)
                .title(request.getTitle())
                .content(request.getContent())
                .build();
        postService.updatePrivateToPublic(dto);
        return ApiResponse.OK(null);
    }


    @Operation(tags="post 좋아요", description = "좋아요 상태를 변경한다.")
    @PostMapping("/like/{postId}")
    public ApiResponse<Boolean> changeLike(@PathVariable("postId") Long postId, @Parameter(hidden = true) @AuthenticationPrincipal User user){
        Long memberId = Long.parseLong(user.getUsername());
        Boolean flag = postLikeService.changeLike(postId, memberId);
        return ApiResponse.OK(flag);
    }
}
