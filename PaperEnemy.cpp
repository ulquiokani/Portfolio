// Fill out your copyright notice in the Description page of Project Settings.

#include "PaperEnemy.h"
#include "GameFramework/CharacterMovementComponent.h"
#include "GameFramework/Controller.h"
#include "Enemy.h"
#include "PlayerControl.h"
#include "Kismet/GameplayStatics.h"
#include "Components/BoxComponent.h"
#include "Components/CapsuleComponent.h"

APaperEnemy::APaperEnemy()
{
	GetCharacterMovement()->bConstrainToPlane = true;
	GetCharacterMovement()->SetPlaneConstraintNormal(FVector(0.0f, -1.0f, 0.0f));
	GetCharacterMovement()->bOrientRotationToMovement = false;

	bUseControllerRotationPitch = false;
	bUseControllerRotationRoll = false;
	bUseControllerRotationYaw = true;

	Range = CreateDefaultSubobject<UBoxComponent>(TEXT("Range"));
	Range->SetupAttachment(RootComponent);
	Range->bGenerateOverlapEvents = true;
	Range->OnComponentBeginOverlap.AddDynamic(this, &APaperEnemy::OnPlayerOverlap);

	GetCapsuleComponent()->OnComponentHit.AddDynamic(this, &APaperEnemy::OnPlayerHit);
}

void APaperEnemy::Tick(float DeltaSeconds) {
	Super::Tick(DeltaSeconds);
	Direction();
}

void APaperEnemy::BeginPlay()
{
	Super::BeginPlay();
	Super::BeginPlay();
	EnemyController = Cast<AEnemy>(GetController());
	Player = Cast<APlayerControl>(UGameplayStatics::GetPlayerCharacter(this, 0));
}

void APaperEnemy::OnPlayerOverlap(UPrimitiveComponent * OverlappedComp, AActor * OtherActor, UPrimitiveComponent * OtherComp, int32 OtherBodyIndex, bool bFromSweep, const FHitResult & SweepResult)
{
	if (OtherActor->IsA(APlayerControl::StaticClass())) {
		EnemyController->FollowPlayer(Player);
	}
}

void APaperEnemy::OnPlayerHit(UPrimitiveComponent * HitComponent, AActor * OtherActor, UPrimitiveComponent * OtherComponent, FVector NormalImpulse, const FHitResult & Hit)
{
	if (OtherActor->IsA(APlayerControl::StaticClass())) {
		APlayerControl* Player = Cast<APlayerControl>(OtherActor);
		Player->ReduceHealth();
	}
}

void APaperEnemy::Direction()
{
	const FVector PlayerVelocity = GetVelocity();
	float Direction = PlayerVelocity.X;

	if (Controller != nullptr) {
		if (Direction < 0.0f) {
			Controller->SetControlRotation(FRotator(0.0, 180, 0.0f));
		}
		else if (Direction > 0.0f) {
			Controller->SetControlRotation(FRotator(0.0f, 0.0f, 0.0f));
		}
	}
}
