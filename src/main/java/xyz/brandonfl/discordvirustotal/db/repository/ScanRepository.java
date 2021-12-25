package xyz.brandonfl.discordvirustotal.db.repository;


import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import xyz.brandonfl.discordvirustotal.db.entity.ScanEntity;

public interface ScanRepository extends JpaRepository<ScanEntity, Long> {
  List<ScanEntity> getAllByResourceIn(List<String> resources);
}
