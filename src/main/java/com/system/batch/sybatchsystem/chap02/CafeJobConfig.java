package com.system.batch.sybatchsystem.chap02;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration // 메서드 내부에 @Bean 메서드들을 싱글콘으로 보장하고, 메서드 호출을 여러번해도 동일한 Bean 으로 재사용되도록 @Component 이 아닌, @Configuration 으로 했다. (Step/Job이 의도치 않게 여러 개 생성될 수 있으므로, @Configuration 권장)  | Spring Batch 는 Job, Step, JobRepository 같은 것들이 정확히 하나로 관리되는 게 중요함. 그래서 설정 클래스는 거의 항상 @Configuration 사용
@RequiredArgsConstructor
public class CafeJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    private final int TARGET_COFFEE_COUNT = 5;
    private int madeCoffeeCount = 0;

    // 1. 카페 문 열기 -> openCafeStep
    // 2. 커피 제조 (5잔) -> makeCoffeeStep
    // 3. 카페 문 닫기 (마감 청소 및 퇴근) -> closeCafeStep

    @Bean // 참고) 당연하게도 모든 step과 job은 반드시 빈으로 등록되어 있어야 한다.
    public Step openCafeStep() {
        // Step 을 만들어주는 StepBuilder 활용
        return new StepBuilder("openCafeStep", jobRepository) // 생성자로 (step의 이름, job을 감시하는 감시자or기록원인 jobRepository)을 넘겨주면 된다.
                .tasklet((contribution, chunkContext) -> {
                    // === [해당 step 이 할 일 작성] ===
                    System.out.println("[오픈] 카페 문을 열고 머신을 예열합니다.");

                    // step 에서 더이상 할일이 없고 마무리됐다면(끝났다면), RepeatStatus.FINISHED 를 리턴하면 된다. ( 참고. RepeatStatus.CONTINUABLE 를 리턴하면, 계속하라는 의미이다. )
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step makeCoffeeStep() {
        return new StepBuilder("makeCoffeeStep", jobRepository)
                .tasklet(((contribution, chunkContext) -> {
                    // === [해당 step 이 할 일 작성] ===
                    madeCoffeeCount++;
                    System.out.println("[제조] 음료 " + madeCoffeeCount + "잔 째 완성");

                    if (madeCoffeeCount < TARGET_COFFEE_COUNT) {
                        return RepeatStatus.CONTINUABLE; // 5잔을 만들때까지 step 반복
                    }

                    System.out.println("[완료] 음료 " + TARGET_COFFEE_COUNT + "잔 나왔습니다.");
                    return RepeatStatus.FINISHED;
                }), transactionManager)
                .build();
    }

    @Bean
    public Step closeCafeStep() {
        return new StepBuilder("closeCafeStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    // === [해당 step 이 할 일 작성] ===
                    System.out.println("[마감] 머신을 끄고 퇴근합니다. 오늘도 수고하셨습니다.");

                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    // 해당 step 들을 아우르는 job 등록
    @Bean
    public Job cafeJob(){
        return new JobBuilder("cafeJob", jobRepository)
                .start(openCafeStep())
                .next(makeCoffeeStep())
                .next(closeCafeStep())
                .build();
    }

}

// step 을 어떤 방식으로 실행할 것인가에 대한 2가지 방식
// 1) tasklet 방식
// 2) chunk 방식