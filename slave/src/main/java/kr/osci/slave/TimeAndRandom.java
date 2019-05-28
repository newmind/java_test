package kr.osci.slave;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "random_dst")
public class TimeAndRandom {
	@Id
    @Column(name = "create_time", unique = true, columnDefinition="DATETIME(3)")
    private LocalDateTime create_time;

    @Column(name = "random", nullable = false)
    private int random;

	public TimeAndRandom(LocalDateTime create_time, int random) {
		super();
		this.create_time = create_time;
		this.random = random;
	}

    public LocalDateTime getCreate_time() {
		return create_time;
	}


	public void setCreate_time(LocalDateTime create_time) {
		this.create_time = create_time;
	}


	public int getRandom() {
		return random;
	}


	public void setRandom(int random) {
		this.random = random;
	}

    @Override
    public String toString() {
        return create_time + "\t" + random;
    }

}
