// Fill out your copyright notice in the Description page of Project Settings.

#include "PlayerControl.h"
#include "GameFramework/SpringArmComponent.h"
#include "Camera/CameraComponent.h"
#include "GameFramework/CharacterMovementComponent.h"
#include "Components/CapsuleComponent.h"
#include "GameFramework/Controller.h"
#include "Components/InputComponent.h"
#include "DrawDebugHelpers.h"
#include "Door.h"
#include "PaperFlipbookComponent.h"
#include "PaperEnemy.h"
#include "TimerManager.h"
#include "Kismet/GameplayStatics.h"
#include "Prototype2GameModeBase.h"

APlayerControl::APlayerControl()
{
	//Camera Setup
	PrimaryActorTick.bCanEverTick = true;
	CameraHold = CreateDefaultSubobject<USpringArmComponent>(TEXT("CameraHold"));
	CameraHold->SetupAttachment(RootComponent);
	CameraHold->TargetArmLength = 500.0f;
	CameraHold->SocketOffset = FVector(0.0f, 0.0f, 75.0f);
	CameraHold->bAbsoluteRotation = true;
	CameraHold->bDoCollisionTest = false;
	CameraHold->RelativeRotation = FRotator(0.0f, -90.0f, 0.0f);
	CameraHold->bAbsoluteRotation = true;


	MainCamera = CreateDefaultSubobject<UCameraComponent>(TEXT("MainCamera"));
	MainCamera->ProjectionMode = ECameraProjectionMode::Orthographic;
	MainCamera->OrthoWidth = 2048.0f;
	MainCamera->SetupAttachment(CameraHold, USpringArmComponent::SocketName);
	MainCamera->bUsePawnControlRotation = false;
	MainCamera->bAutoActivate = true;

	//Character Setup
	GetCharacterMovement()->GravityScale = 2.0f;
	GetCharacterMovement()->AirControl = 100.0f;
	GetCharacterMovement()->JumpZVelocity = 1000.0f;
	GetCharacterMovement()->GroundFriction = 3.0f;
	GetCharacterMovement()->MaxWalkSpeed = 1200.0f;
	GetCharacterMovement()->MaxFlySpeed = 600.0f;
	GetCharacterMovement()->bConstrainToPlane = true;
	GetCharacterMovement()->SetPlaneConstraintNormal(FVector(0.0f, -1.0f, 0.0f));
	GetCharacterMovement()->bOrientRotationToMovement = false;

	//Controller Setup
	bUseControllerRotationPitch = false;
	bUseControllerRotationRoll = false;
	bUseControllerRotationYaw = true;
	
	ShotDistance = 500.0f;
	GlideGravity = 0.5f;

	GetCapsuleComponent()->bGenerateOverlapEvents = true;

	CanFire = true;
	ReloadTime = 1.0f;

	PlayerHealth = 3;
	Invincible = false;
	InvicTime = 1.0f;

	isGlide = false;
}

void APlayerControl::SetupPlayerInputComponent(UInputComponent * PlayerInputComponent)
{
	PlayerInputComponent->BindAction("Jump", IE_Pressed, this, &ACharacter::Jump);
	PlayerInputComponent->BindAction("Jump", IE_Released, this, &ACharacter::StopJumping);
	PlayerInputComponent->BindAction("Glide", IE_Pressed, this, &APlayerControl::Glide);
	PlayerInputComponent->BindAction("Glide", IE_Released, this, &APlayerControl::StopGlide);
	PlayerInputComponent->BindAxis("Move", this, &APlayerControl::Move);
	PlayerInputComponent->BindAction("Fire", IE_Pressed, this, &APlayerControl::Gunray);
}

void APlayerControl::Move(float Value)
{
	AddMovementInput(FVector(1.0f, 0.0f, 0.0f), Value);
}

void APlayerControl::Gunray()
{
	if (CanFire) {
		CanFire = false;
		UE_LOG(LogTemp, Warning, TEXT("GUN ACTIVATE"));
		FHitResult* Check = new FHitResult();
		FVector StartTrace = this->GetActorLocation();
		FVector ForwardVector = this->GetActorForwardVector();
		FVector EndTrace = (ForwardVector * ShotDistance) + StartTrace;
		FCollisionQueryParams QP;
		DrawDebugLine(GetWorld(), StartTrace, EndTrace, FColor(255, 0, 0), false, 0.4f);
		if (GetWorld()->LineTraceSingleByChannel(*Check, StartTrace, EndTrace, ECC_Visibility, QP))
		{
			//DrawDebugLine(GetWorld(), StartTrace, EndTrace, FColor(255, 0, 0), true);
			UE_LOG(LogTemp, Warning, TEXT("GUN ACTIVATE 2"));
			if (Check->GetActor() != NULL) {
				if (Check->GetActor()->IsA(ADoor::StaticClass())) {
					Check->GetActor()->Destroy();
					Cast<APrototype2GameModeBase>(GetWorld()->GetAuthGameMode())->AddScore();
				} else if (Check->GetActor()->IsA(APaperEnemy::StaticClass())) {
					Check->GetActor()->Destroy();
					Cast<APrototype2GameModeBase>(GetWorld()->GetAuthGameMode())->AddScore();
				}
			}
		}
		GetWorld()->GetTimerManager().SetTimer(ShotTimer, this, &APlayerControl::ShotReset, ReloadTime, false);
	}
}

void APlayerControl::Jump()
{
	bPressedJump = true;
	JumpKeyHoldTime = 0.0f;
}

void APlayerControl::Direction()
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

void APlayerControl::Tick(float DeltaSeconds) {
	Super::Tick(DeltaSeconds);
	Direction();
	if (isGlide) {
		AddMovementInput(FVector(1.0f, 0.0f, 0.0f));
	}
}

void APlayerControl::Glide() {
	isGlide = true;
	if (GetCharacterMovement()->IsFalling()) {
		FVector currentvelocity = this->GetVelocity();
		GetCharacterMovement()->Velocity = FVector(currentvelocity.X, currentvelocity.Y, 0);
		GetCharacterMovement()->GravityScale = GlideGravity;
	}

}

void APlayerControl::StopGlide()
{
	isGlide = false;
	UE_LOG(LogTemp, Warning, TEXT("StopGlide"));
	GetCharacterMovement()->GravityScale = 2.0f;
}

void APlayerControl::ShotReset()
{
	CanFire = true;
	GetWorldTimerManager().ClearTimer(ShotTimer);
}

void APlayerControl::InvicReset()
{
	Invincible = false;
	GetSprite()->SetSpriteColor(FLinearColor(1, 1, 1, 1));
	GetWorldTimerManager().ClearTimer(DamageTimer);
}

void APlayerControl::ChangeMovementMode(bool flying)
{
	if (flying) {
		GetCharacterMovement()->SetMovementMode(MOVE_Flying);
	}
	else {
		GetCharacterMovement()->SetMovementMode(MOVE_Walking);
	}
}

void APlayerControl::ReduceHealth()
{
	if (!Invincible) {
		GetSprite()->SetSpriteColor(FLinearColor(1, 1, 1, 0.5));
		Invincible = true;
		UE_LOG(LogTemp, Warning, TEXT("PlayerHit"));
		PlayerHealth--;
		if (PlayerHealth == 0) {
			UGameplayStatics::OpenLevel(GetWorld(), "MainMenu");
		}
		GetWorld()->GetTimerManager().SetTimer(DamageTimer, this, &APlayerControl::InvicReset, InvicTime, false);
	}
	
}



